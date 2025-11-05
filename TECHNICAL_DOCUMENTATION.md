# Employee Attendance Management System - Technical Documentation

This document provides a comprehensive technical overview of the Employee Attendance Management System, including its architecture, setup instructions, and operational flow.

## 1. Project Folder Structure

The project is organized into two main directories: `backend` and `frontend`.

```
/
├── backend/
│   ├── api-gateway/
│   ├── attendance-service/
│   ├── auth-service/
│   ├── common/
│   ├── discovery-server/
│   ├── employee-service/
│   ├── notification-service/
│   ├── report-service/
│   └── pom.xml  (Parent POM for all backend services)
│
└── frontend/
    ├── src/
    ├── package.json
    └── ...
```

## 2. Installation and Setup

### Prerequisites
- **Java:** Version 17 or higher
- **Maven:** Version 3.8 or higher
- **Node.js:** Version 18 or higher
- **Docker:** For running external services (MySQL, Redis, Kafka)

### Backend Setup

1.  **Start External Services:**
    A `docker-compose.yml` file should be used to start the required services.
    ```bash
    docker-compose up -d mysql redis kafka
    ```

2.  **Build the Project:**
    Navigate to the `backend` directory and run the Maven build command.
    ```bash
    cd backend
    mvn clean install
    ```

### Frontend Setup

1.  **Install Dependencies:**
    Navigate to the `frontend` directory and install the required npm packages.
    ```bash
    cd frontend
    npm install
    ```

2.  **Environment Configuration:**
    Create a `.env.local` file in the `frontend` directory with the following content:
    ```
    VITE_API_BASE_URL=http://localhost:8080/api
    ```

## 3. System Architecture and Flow

### Service Discovery and Communication
- **Eureka Server (`discovery-server`):** All microservices register themselves with Eureka upon startup.
- **API Gateway (`api-gateway`):** The single entry point for all frontend requests. It uses Eureka to discover and route requests to the appropriate microservices.

### JWT Authentication Flow
1.  The user logs in from the React frontend.
2.  The request is sent to the **API Gateway**, which routes it to the **Auth Service**.
3.  The **Auth Service** validates the credentials and returns a JWT containing the user's email and role (e.g., `ADMIN`, `EMPLOYEE`).
4.  The frontend stores the JWT in local storage.
5.  For all subsequent requests, an Axios interceptor attaches the JWT to the `Authorization` header.
6.  The **API Gateway** intercepts and validates the JWT before forwarding the request to the downstream service.

### Role-Based Access Control (RBAC)
- **Frontend:** The React app uses a `ProtectedRoute` component that checks the user's role from the Redux store. It only renders a route if the user's role is in the `allowedRoles` list.
- **Backend:** The **API Gateway** performs initial routing based on role for sensitive endpoints. Downstream services (e.g., `employee-service`) perform a second layer of authorization, re-validating the role from the JWT.

### Kafka Event Handling and Redis Usage
- **Check-in/Check-out:** When a user checks in or out, the **Attendance Service** saves the record to its database and publishes an `Attendance` event to the `attendance-topic` in Kafka.
- **Report Generation:** The **Report Service** consumes events from the `attendance-topic` to generate and update attendance reports.
- **Notifications:** The **Notification Service** also consumes from the `attendance-topic`. If it detects a "LATE" check-in, it sends an email alert.
- **Online Employees:** The **Attendance Service** uses **Redis** to maintain a real-time set of currently checked-in (online) employees.

## 4. How to Run the System

1.  **Start Backend Services:**
    Run each microservice in a separate terminal.
    ```bash
    # Terminal 1: Discovery Server
    java -jar backend/discovery-server/target/*.jar

    # Terminal 2: API Gateway
    java -jar backend/api-gateway/target/*.jar

    # Terminal 3: Auth Service
    java -jar backend/auth-service/target/*.jar

    # ... and so on for all other backend services
    ```

2.  **Start Frontend Development Server:**
    ```bash
    cd frontend
    npm run dev
    ```
    The application will be available at `http://localhost:5173`.

## 5. API and Routing Details

### Frontend Routes
- `/login`: User login page.
- `/register`: User registration page.
- `/admin/dashboard`: Dashboard for admin users.
- `/admin/employees`: Employee management (CRUD) page.
- `/admin/reports`: Attendance reports page.
- `/employee/dashboard`: Dashboard for regular employees (for check-in/out).

### Backend API Endpoints

#### API Gateway (`http://localhost:8080`)
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/employee/all`
- `POST /api/employee`
- `PUT /api/employee/{id}`
- `DELETE /api/employee/{id}`
- `POST /api/attendance/checkin`
- `POST /api/attendance/checkout`
- `GET /api/report/summary/monthly`

## 6. Libraries and Dependencies

### Frontend (`npm install`)
- `@reduxjs/toolkit`, `react-redux`, `redux-persist`
- `react`, `react-dom`
- `react-router-dom`
- `axios`
- `tailwindcss`, `shadcn-ui`, `lucide-react`
- `jwt-decode`
- `vite`

### Backend (Maven Dependencies)
- **Spring Boot:** `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`
- **Spring Cloud:** `spring-cloud-starter-netflix-eureka-client`, `spring-cloud-starter-gateway`
- **Database:** `mysql-connector-java`
- **Messaging:** `spring-kafka`
- **Caching:** `spring-boot-starter-data-redis`
- **API Docs:** `springdoc-openapi-starter-webmvc-ui`
- **JWT:** `jjwt-api`, `jjwt-impl`, `jjwt-jackson`

## 7. Troubleshooting Common Errors

- **503 Service Unavailable:** This usually means the API Gateway cannot find the downstream microservice in Eureka. Ensure the target service is running and has successfully registered with the Eureka server.
- **Database Connection Errors:** Verify that the MySQL container is running and that the `application.yml` of each service has the correct database URL, username, and password.
- **Kafka Connection Errors:** Ensure the Kafka container is running and the `bootstrap-servers` property in the `application.yml` of the `attendance-service` and `report-service` is correct.
- **401 Unauthorized / Token Expiry:** If you receive a 401 error, your JWT has likely expired. The frontend should be configured to automatically log the user out and redirect them to the login page.
