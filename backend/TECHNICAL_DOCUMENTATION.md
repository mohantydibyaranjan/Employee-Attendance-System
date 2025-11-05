# Employee Attendance System - Technical Documentation

This document provides a detailed technical overview of the Employee Attendance System, a microservices-based application built with Spring Boot.

## Table of Contents
1.  [Architecture Overview](#1-architecture-overview)
2.  [Service Interactions](#2-service-interactions)
3.  [Security: JWT Flow](#3-security-jwt-flow)
4.  [Asynchronous Communication: Kafka Event Flow](#4-asynchronous-communication-kafka-event-flow)
5.  [Real-Time Presence: Redis](#5-real-time-presence-redis)
6.  [Real-Time UI Updates: WebSocket Integration](#6-real-time-ui-updates-websocket-integration)
7.  [Testing Strategy](#7-testing-strategy)

---

## 1. Architecture Overview

The system is designed using a microservices architecture to ensure scalability, maintainability, and separation of concerns. Each service is a standalone Spring Boot application with its own responsibilities and, in some cases, its own database schema.

### Architecture Diagram

```
[Client] -> [API Gateway] -> [Discovery Server]
                     |
                     +--> [Auth Service]
                     +--> [Employee Service]
                     +--> [Attendance Service] -> [Kafka] -> [Report Service]
                     |                                |
                     +--> [Report Service]           +--> [Notification Service] -> [Email]
                                                            |
                                                            +--> [WebSocket] -> [Admin Dashboard]
```

### Core Components:

*   **Discovery Server (Eureka):** All microservices register themselves with the Eureka server, which provides a dynamic registry for service discovery. This allows services to locate each other without hardcoded URLs.
*   **API Gateway (Spring Cloud Gateway):** This is the single entry point for all client requests. It is responsible for:
    *   **Routing:** Directing incoming requests to the appropriate microservice.
    *   **Centralized Security:** Validating JWTs and performing initial role-based access control.
    *   **Load Balancing:** Distributing requests across multiple instances of a service.
*   **Auth Service:** Handles user authentication.
    *   **Responsibilities:** User registration and login (via email and password).
    *   **Output:** Generates a JWT upon successful authentication.
*   **Employee Service:** Manages employee data.
    *   **Responsibilities:** Provides CRUD (Create, Read, Update, Delete) operations for employee records.
    *   **Access Control:** All endpoints are restricted to users with the `ADMIN` role.
*   **Attendance Service:** Manages employee check-ins and check-outs.
    *   **Responsibilities:** Records attendance, calculates working hours, and tracks real-time employee presence.
    *   **Events:** Publishes attendance events to a Kafka topic.
*   **Report Service:** Aggregates attendance data to generate reports.
    *   **Responsibilities:** Consumes attendance events from Kafka and generates daily and monthly summaries.
*   **Notification Service:** Sends real-time alerts.
    *   **Responsibilities:** Consumes attendance events from Kafka and sends email notifications for events like late check-ins. It can also be configured to push real-time updates to an admin dashboard via WebSockets.
*   **Kafka:** A distributed event streaming platform used for asynchronous communication between services.
*   **Redis:** An in-memory data store used to track real-time employee presence.
---

## 2. Service Interactions

This section details the sequence of interactions for key use cases.

### Use Case 1: User Login

1.  **Client Request:** The user sends a `POST` request to `/auth/login` with their email and password.
2.  **API Gateway:** The gateway receives the request and, since the path is not protected, routes it directly to the **Auth Service**.
3.  **Auth Service:**
    *   Validates the credentials against the user database.
    *   If valid, generates a JWT containing the user's ID, email, and role.
    *   Returns the JWT to the client.

### Use Case 2: Employee Check-In

1.  **Client Request:** The employee sends a `POST` request to `/attendance/checkin` with a valid JWT in the `Authorization` header.
2.  **API Gateway:**
    *   Validates the JWT's signature and expiration.
    *   Since the path is protected, it inspects the token's claims.
    *   Routes the request to the **Attendance Service**.
3.  **Attendance Service:**
    *   Re-validates the JWT and extracts the employee's ID.
    *   Checks if the check-in is late (e.g., after 9:30 AM).
    *   Creates a new `Attendance` record with a status of "PRESENT" or "LATE" and saves it to the database.
    *   Adds the employee's ID to a `Set` in **Redis** to mark them as "online."
    *   Publishes the `Attendance` object to the `attendance-topic` in **Kafka**.
    *   Returns a success response to the client.

### Use Case 3: Admin Fetches a Monthly Report

1.  **Client Request:** An admin user sends a `GET` request to `/report/summary/monthly` with a valid JWT and the employee's ID as a query parameter.
2.  **API Gateway:**
    *   Validates the JWT.
    *   Extracts the user's role from the token. Since the path is restricted to admins, it verifies the role is `ADMIN`.
    *   If authorized, it routes the request to the **Report Service**.
3.  **Report Service:**
    *   Re-validates the JWT and confirms the admin role.
    *   Retrieves the pre-aggregated monthly summary from its own database.
    *   Returns the report data to the client.
---

## 3. Security: JWT Flow

The system uses a "zero-trust" security model based on JSON Web Tokens (JWT).

### JWT Generation

1.  **Authentication:** A user authenticates with the **Auth Service** using their email and password.
2.  **Token Creation:** Upon successful authentication, the **Auth Service** generates a JWT.
3.  **Claims:** The JWT payload contains the following claims:
    *   `sub` (Subject): The user's email.
    *   `id`: The user's unique ID.
    *   `role`: The user's role (e.g., `ADMIN`, `EMPLOYEE`).
    *   `iat` (Issued At): The timestamp when the token was created.
    *   `exp` (Expiration): The timestamp when the token will expire.
4.  **Signing:** The token is signed with a secret key to ensure its integrity.

### JWT Validation and Authorization

1.  **API Gateway (Initial Validation):**
    *   For every request to a protected route, the **API Gateway** inspects the `Authorization` header.
    *   It validates the JWT's signature and expiration date.
    *   It performs an initial role-based authorization check for routes that are restricted to specific roles (e.g., `/employee/**` requires `ADMIN`).
2.  **Downstream Services (Full Validation):**
    *   Even after the gateway's initial check, each downstream microservice is responsible for re-validating the JWT.
    *   The service extracts the user's ID and role from the token's claims.
    *   It uses this information to perform its own fine-grained authorization checks before allowing access to its resources. This adherence to a zero-trust model ensures that each service is independently secure.
---

## 4. Asynchronous Communication: Kafka Event Flow

The system uses Apache Kafka for reliable, asynchronous communication between services. This decouples the services and improves fault tolerance.

### Kafka Topic: `attendance-topic`

*   **Purpose:** This is the central topic for all attendance-related events.
*   **Payload:** The messages on this topic are serialized `Attendance` objects.

### Producers

*   **Attendance Service:** This is the primary producer for the `attendance-topic`.
    *   **On Check-In:** After a successful check-in, it publishes the new `Attendance` record.
    *   **On Check-Out:** After a successful check-out, it publishes the updated `Attendance` record, now including the `checkOutTime` and `totalHours`.

### Consumers

*   **Report Service:**
    *   **Consumer Group:** `report-group`
    *   **Action:** It listens for all messages on the `attendance-topic`. For each message, it updates its own database of aggregated attendance data, which is used to generate the monthly summaries.
*   **Notification Service:**
    *   **Consumer Group:** `notification-group`
    *   **Action:** It listens for all messages on the `attendance-topic`. If it receives an `Attendance` object with a `status` of "LATE," it triggers an email notification to the employee. It can also be configured to push a WebSocket message to an admin dashboard.
---

## 5. Real-Time Presence: Redis

Redis is used as a fast, in-memory data store to track which employees are currently checked in.

*   **Data Structure:** A `Set` with the key `online_employees`.
*   **On Check-In:** When an employee checks in, the **Attendance Service** adds their `employeeId` to the `online_employees` `Set`.
*   **On Check-Out:** When an employee checks out, their `employeeId` is removed from the `Set`.
*   **Use Case:** This allows for a very fast lookup of all currently online employees, which can be used to display a real-time presence dashboard for administrators.
---

## 6. Real-Time UI Updates: WebSocket Integration

WebSockets are used to push real-time updates from the server to a client-side admin dashboard, eliminating the need for polling.

### WebSocket Flow

1.  **Client Connection:** An admin dashboard client establishes a WebSocket connection to the **Notification Service**.
2.  **Kafka Consumer:** The **Notification Service** consumes attendance events from the `attendance-topic` in real-time.
3.  **Message Broadcasting:** For each event, the **Notification Service** processes the message and broadcasts a payload to all connected WebSocket clients. This payload can be used to:
    *   Update the list of "online" employees.
    *   Show a live feed of check-in and check-out activities.
    *   Display instant alerts for late check-ins.
---

## 7. Testing Strategy

A multi-layered testing strategy is employed to ensure the correctness and reliability of the system.

### Unit Testing

*   **Frameworks:** JUnit 5, Mockito
*   **Scope:** Each service has its own suite of unit tests.
*   **Focus:**
    *   Testing individual components in isolation (e.g., controllers, services, utility classes).
    *   Mocking dependencies (e.g., repositories, KafkaTemplate, RedisTemplate) to ensure that only the unit under test is being evaluated.
    *   Verifying business logic, edge cases, and exception handling.

### Integration Testing

*   **Frameworks:** Spring Boot's `@SpringBootTest`, Testcontainers
*   **Scope:** Within each microservice, integration tests are used to verify the interactions between different layers of the application.
*   **Focus:**
    *   Testing the flow from the controller to the service, repository, and database.
    *   Using Testcontainers to spin up real instances of dependencies like MySQL, Kafka, and Redis in a Docker environment. This ensures that the application is tested against a realistic environment.
    *   Verifying that Kafka events are correctly produced and consumed.

### End-to-End (E2E) Testing

*   **Frameworks:** Postman, Newman, or a dedicated E2E testing framework like Karate.
*   **Scope:** E2E tests are run against a fully deployed environment with all microservices running.
*   **Focus:**
    *   Testing the full request lifecycle, from the client, through the API Gateway, to the downstream services.
    *   Verifying the correctness of the entire system for key user flows (e.g., login, check-in, check-out, report generation).
    *   Ensuring that the security policies and routing rules in the API Gateway are working as expected.
---
