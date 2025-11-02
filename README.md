# Enterprise Employee Attendance System: Technical Documentation

This document provides a complete technical, functional, and architectural overview of the Employee Attendance System. It is intended for developers, testers, and system architects responsible for maintaining, extending, and testing the application.

## 1. Architectural Vision & Principles

The system is designed as a scalable, resilient, and secure microservices application adhering to modern cloud-native principles.

*   **Microservices Architecture**: The system is decomposed into small, independent services, each responsible for a specific business capability. This promotes modularity, independent deployment, and technological diversity.
*   **Zero-Trust Security**: Security is not an afterthought. The architecture follows a "zero-trust" model where no service implicitly trusts another. The API Gateway performs initial authentication, but each downstream service is responsible for re-validating the JWT and enforcing its own authorization rules.
*   **Asynchronous Communication**: For non-blocking, real-time operations, the system uses Apache Kafka for event-driven communication. This decouples services like `Attendance` from `Report` and `Notification`, enhancing resilience.
*   **Centralized Edge Control**: A Spring Cloud Gateway acts as the single entry point, handling cross-cutting concerns like routing, initial authentication, and rate limiting (feature-pending), providing a unified and secure public API.
*   **Stateless Services**: All backend services are designed to be stateless, allowing for easy horizontal scaling. State, where necessary, is managed externally in Redis (for session data like online status) or MySQL (for persistence).

---

## 2. Technology Stack

| Component             | Technology                               | Purpose                                                 |
| --------------------- | ---------------------------------------- | ------------------------------------------------------- |
| **Core Framework**    | Spring Boot 3 & Java 17                  | High-performance, modern application development        |
| **Service Discovery** | Spring Cloud Netflix Eureka              | Dynamic registration and discovery of microservices     |
| **API Gateway**       | Spring Cloud Gateway (Reactive)          | Centralized routing, security, and request filtering    |
| **Security**          | Spring Security + JJWT                   | Authentication and Authorization using JSON Web Tokens  |
| **Database**          | MySQL                                    | Relational data persistence for all services            |
| **Messaging Queue**   | Apache Kafka                             | Asynchronous, event-driven communication                |
| **In-Memory Cache**   | Redis                                    | Caching real-time data (e.g., online employees)         |
| **API Documentation** | Springdoc OpenAPI 3 (Swagger)            | Interactive API documentation for each microservice     |
| **Logging**           | SLF4J + Logback                          | Structured, distributed logging with trace IDs          |
| **DTOs & Validation** | Lombok & Jakarta Bean Validation         | Reducing boilerplate code and ensuring data integrity   |

---

## 3. Microservice Deep Dive

Each microservice is a self-contained Spring Boot application with its own dedicated database schema.

#### 3.1. Discovery Server (`discovery-server`)
*   **Purpose**: Acts as the service registry. All other microservices register themselves here on startup, allowing them to discover each other by service name instead of hardcoded IP addresses.
*   **Port**: `8761`

#### 3.2. API Gateway (`api-gateway`)
*   **Purpose**: The single entry point for all external requests. It is responsible for:
    1.  **Routing**: Forwarding requests to the correct downstream service (e.g., `/api/employee/**` -> `employee-service`).
    2.  **Authentication**: Performing the initial validation of the JWT.
    3.  **Logging**: Generating a unique `traceId` for each request to enable distributed tracing.
*   **Port**: `8080`

#### 3.3. Auth Service (`auth-service`)
*   **Purpose**: Manages user identity.
*   **Key Endpoints**:
    *   `POST /api/auth/register`: Creates a new user (`ADMIN` or `EMPLOYEE`).
    *   `POST /api/auth/login`: Authenticates a user and generates a signed JWT.
*   **Core Logic**: Uses the shared `JwtUtil` to create a JWT containing `userId`, `email`, and `role` claims upon successful login.
*   **Port**: `8081`

#### 3.4. Employee Service (`employee-service`)
*   **Purpose**: Handles all CRUD operations for employee records.
*   **Authorization**: **ADMIN Only**. Every endpoint programmatically validates the JWT to ensure the user has the `ADMIN` role.
*   **Key Endpoints**:
    *   `POST /api/employee`: Create a new employee.
    *   `GET /api/employee/{id}`: Get an employee by ID.
    *   `GET /api/employee/filter`: Advanced search with filtering, sorting, and pagination.
*   **Port**: `8082`

#### 3.5. Attendance Service (`attendance-service`)
*   **Purpose**: Manages employee attendance records.
*   **Authorization**: **ADMIN** or **EMPLOYEE**.
*   **Core Logic**:
    *   On check-in/check-out, it extracts the `employeeId` directly from the JWT to ensure users can only act on their own behalf.
    *   Publishes an `attendance` event to a Kafka topic after every action.
    *   Adds/removes the `employeeId` from a Redis set to track online users.
*   **Key Endpoints**:
    *   `POST /api/attendance/checkin`
    *   `POST /api/attendance/checkout`
    *   `GET /api/attendance/history/{id}`
*   **Port**: `8083`

#### 3.6. Report Service (`report-service`)
*   **Purpose**: Consumes attendance events to generate aggregated reports.
*   **Authorization**: **ADMIN Only**.
*   **Core Logic**: Listens to the `attendance-topic` on Kafka. When an event is received, it updates its internal summary tables.
*   **Key Endpoints**:
    *   `GET /api/report/summary/monthly`
*   **Port**: `8084`

#### 3.7. Notification Service (`notification-service`)
*   **Purpose**: Consumes attendance events to send notifications.
*   **Core Logic**: Listens to the `attendance-topic` and logs a warning if a check-in occurs after the designated start time (e.g., 9:30 AM).
*   **Port**: `8085`

---

## 4. Security Architecture: A Zero-Trust Approach

The system's security is multi-layered, ensuring that a breach in one part of the system does not compromise the whole.

#### 4.1. JWT Generation
*   The `auth-service` is the sole authority for creating JWTs.
*   The JWT payload is standardized to contain:
    ```json
    {
      "sub": "username",
      "role": "ADMIN",
      "employeeId": 101,
      "email": "admin@example.com",
      "iat": 1672531200,
      "exp": 1672617600
    }
    ```

#### 4.2. Layer 1: API Gateway Authentication
*   The `JwtAuthenticationFilter` in the gateway performs the first-pass security check on all protected routes.
*   It validates the JWT's signature and expiration using the shared secret.
*   If the token is invalid, the request is immediately rejected with a `401 Unauthorized` status.
*   Crucially, it **forwards the original `Authorization` header** downstream without making any authorization decisions itself.

#### 4.3. Layer 2: Downstream Service Authorization
*   Each microservice (`employee`, `attendance`, `report`) re-validates the JWT and performs its own authorization.
*   **This is done programmatically in each controller method**, without using Spring Security annotations, as per the project's conventions.
*   **Example from `EmployeeController`**:
    ```java
    // All endpoints accept the Authorization header
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(
        @RequestHeader("Authorization") String authHeader,
        @Valid @RequestBody EmployeeDto employeeDto) {

        // Manual, programmatic role check
        checkAdminRole(authHeader);

        // ... proceed with business logic
    }

    private void checkAdminRole(String authHeader) {
        // Use the shared JwtUtil to extract the role from the token
        String role = jwtUtil.extractRole(authHeader.substring(7));
        if (!"ADMIN".equals(role)) {
            // Throw a custom exception handled by the global exception handler
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }
    }
    ```

---

## 5. API Conventions & Standards

To ensure consistency and predictability, all services adhere to the following conventions.

#### 5.1. Unified API Response
*   **Success Response**: All successful `200 OK` responses are wrapped in a standard `ApiResponse<T>` object.
    ```json
    {
      "success": true,
      "message": "Employee created successfully",
      "data": { "id": 101, "name": "John Doe" },
      "timestamp": "2025-11-02T05:00:00Z"
    }
    ```
*   **Error Response**: All exceptions are handled by a `@RestControllerAdvice` and formatted into a standard `ErrorResponse`.
    ```json
    {
      "success": false,
      "message": "Validation failed",
      "errors": [
        { "field": "email", "error": "Invalid email format" }
      ],
      "timestamp": "2025-11-02T05:02:00Z",
      "path": "/api/employee"
    }
    ```

#### 5.2. Distributed Logging with Trace ID
*   The API Gateway generates a `traceId` for every request. This ID is passed in a header to downstream services.
*   Each microservice is configured with **Logback** to produce structured logs that include this `traceId`, allowing you to correlate all log entries for a single request across multiple services.
*   **Log Format**:
    `[%d{yyyy-MM-dd HH:mm:ss}] [TRACE_ID:%X{traceId}] [%level] [%logger{36}] - %msg%n`
*   **Example Log Entry**:
    `[2025-11-02 05:05:00] [TRACE_ID:abc-123] [INFO] [EmployeeService] - Creating new employee...`

---

## 6. Local Development and Testing Guide

(This section is a summary of the full testing guide from the previous `README.md` and can be expanded as needed)

#### 6.1. Prerequisites
-   Java 17, Maven
-   Local instances of MySQL, Redis, and Apache Kafka (with Zookeeper) running.

#### 6.2. Database Setup
Manually create the required schemas in MySQL:
```sql
CREATE DATABASE auth_db;
CREATE DATABASE employee_db;
CREATE DATABASE attendance_db;
CREATE DATABASE report_db;
```

#### 6.3. Running the System
1.  **Build**: `mvn clean install`
2.  **Run**: Start each service in a separate terminal using `mvn spring-boot:run`, in the following order:
    1.  `discovery-server`
    2.  `api-gateway`
    3.  `auth-service`
    4.  All other services...

---

## 7. API Documentation (Swagger)

Each microservice generates its own interactive OpenAPI 3 documentation. Access the Swagger UI at:
-   **Auth Service**: `http://localhost:8081/swagger-ui/index.html`
-   **Employee Service**: `http://localhost:8082/swagger-ui/index.html`
-   ...and so on for each service.

Use the "Authorize" button in the UI to add your JWT for testing secured endpoints.
