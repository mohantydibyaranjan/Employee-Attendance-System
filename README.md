# Enterprise Employee Attendance System

This project is a complete, production-ready Spring Boot 3 microservices backend for an Employee Attendance System. It is built with enterprise-grade features including centralized security, structured logging, advanced data querying, and comprehensive API documentation.

## Table of Contents
1.  [Architecture Overview](#architecture-overview)
2.  [Microservices](#microservices)
3.  [High-Level Code Flow](#high-level-code-flow)
4.  [Getting Started](#getting-started)
5.  [Testing Guide: Data, URLs, and Roles](#testing-guide-data-urls-and-roles)
6.  [API Documentation (Swagger)](#api-documentation-swagger)

---

## Architecture Overview

The system is designed using a modern microservices architecture, promoting scalability, resilience, and maintainability.

-   **Spring Boot 3 & Java 17**: The core framework for building robust and high-performance microservices.
-   **Spring Cloud Gateway**: A single entry point (API Gateway) for all client requests, responsible for routing, security, and cross-cutting concerns.
-   **Eureka Server**: For dynamic service discovery, allowing services to find and communicate with each other without hardcoded locations.
-   **Spring Security + JWT**: Centralized authentication and authorization at the API Gateway. JWTs carry user identity (`employeeId`) and `role`, which are securely propagated to downstream services.
-   **Apache Kafka**: A distributed event streaming platform used for asynchronous communication. The `Attendance Service` produces events (like check-in/check-out), which are consumed by the `Report Service` and `Notification Service`.
-   **Redis**: An in-memory data store used for high-performance caching, specifically for tracking which employees are currently online.
-   **MySQL**: The relational database for data persistence. Each microservice has its own dedicated schema.
-   **Docker Compose**: For orchestrating the entire application stack, including all services and backing infrastructure (MySQL, Kafka, Redis).

---

## Microservices

-   **API Gateway**: The front door to the system. It handles request routing and enforces all security rules.
-   **Discovery Server**: The service registry where all other services register themselves.
-   **Auth Service**: Manages user registration and login, and issues JWTs.
-   **Employee Service**: Handles CRUD operations for employee data. (Admin only)
-   **Attendance Service**: Manages employee check-ins and check-outs, publishes events to Kafka, and updates Redis.
-   **Report Service**: Consumes Kafka events to generate attendance reports. (Admin only)
-   **Notification Service**: Consumes Kafka events to send notifications (e.g., for late check-ins).

---

## High-Level Code Flow

1.  **Client Request**: A client sends a request to the **API Gateway** (`:8080`). For protected endpoints, it must include a JWT in the `Authorization: Bearer <token>` header.

2.  **API Gateway Interception**:
    *   A reactive `TraceIdFilter` generates a unique `traceId` and adds it to the request headers.
    *   The `JwtAuthenticationFilter` validates the JWT, checks the user's `role` against the required permissions for the endpoint (RBAC), and injects `X-Employee-Id` and `X-Role` headers into the request. If security checks fail, it returns a `401` or `403` error.

3.  **Downstream Service**:
    *   The Gateway forwards the enriched request to the target microservice (e.g., `employee-service`).
    *   A servlet `TraceIdFilter` in the downstream service reads the `traceId` header and adds it to the logging context (MDC), ensuring all logs for the request are correlated.
    *   The controller receives the request, safely reads the user's identity from the headers, and processes the request.
    *   Exceptions are handled globally by a `@RestControllerAdvice`, which returns a standardized error response.
    *   Successful responses are wrapped in a standard `ApiResponse` format.

---

## Getting Started

You will need Docker and Docker Compose installed to run the system.

**1. Build the Project**
First, build all the microservice JAR files using Maven.
```bash
mvn clean install
```

**2. Run the System**
Use Docker Compose to start all the services and backing infrastructure.
```bash
docker-compose up --build
```
This command will build the Docker images for each service and start the entire stack.

---

## Testing Guide: Data, URLs, and Roles

#### Step 1: Create Test Users

**1. Register an ADMIN User**
```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "adminuser",
    "password": "password123",
    "email": "admin@example.com",
    "role": "ADMIN"
}'
```

**2. Register an EMPLOYEE User**
```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "employeeuser",
    "password": "password456",
    "email": "employee@example.com",
    "role": "EMPLOYEE"
}'
```

#### Step 2: Get JWT Tokens

**1. Get ADMIN Token**
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username": "adminuser", "password": "password123"}'
```
> **Copy the `token`** from the response. Let's call it `ADMIN_TOKEN`.

**2. Get EMPLOYEE Token**
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username": "employeeuser", "password": "password456"}'
```
> **Copy the `token`** from the response. Let's call it `EMPLOYEE_TOKEN`.

#### Step 3: Test Role-Based Access

**Scenario 1: Admin-Only Routes (`/employee/**`)**
*   **As ADMIN (Should Succeed)**:
    ```bash
    curl -X GET http://localhost:8080/employee/all -H "Authorization: Bearer <ADMIN_TOKEN>"
    ```
*   **As EMPLOYEE (Should Fail with 403 Forbidden)**:
    ```bash
    curl -X GET http://localhost:8080/employee/all -H "Authorization: Bearer <EMPLOYEE_TOKEN>"
    ```

**Scenario 2: Employee/Admin Routes (`/attendance/**`)**
*   **As EMPLOYEE (Should Succeed)**:
    ```bash
    curl -X POST http://localhost:8080/attendance/checkin -H "Authorization: Bearer <EMPLOYEE_TOKEN>"
    ```
*   **As ADMIN (Should Succeed)**:
    ```bash
    curl -X POST http://localhost:8080/attendance/checkin -H "Authorization: Bearer <ADMIN_TOKEN>"
    ```

**Scenario 3: No Token (Should Fail with 401 Unauthorized)**
```bash
curl -X GET http://localhost:8080/employee/all
```

---

## API Documentation (Swagger)

Each microservice generates its own OpenAPI 3 documentation. Once the system is running, you can access the Swagger UI for each service at the following URLs:

-   **Auth Service**: `http://localhost:8081/swagger-ui/index.html`
-   **Employee Service**: `http://localhost:8082/swagger-ui/index.html`
-   **Attendance Service**: `http://localhost:8083/swagger-ui/index.html`
-   **Report Service**: `http://localhost:8084/swagger-ui/index.html`

The Swagger UI includes an "Authorize" button where you can paste a JWT bearer token to test secured endpoints directly from the documentation.
