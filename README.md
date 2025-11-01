# Enterprise Employee Attendance System

This project is a complete, production-ready Spring Boot 3 microservices backend for an Employee Attendance System. It is built with enterprise-grade features including centralized security, structured logging, advanced data querying, and comprehensive API documentation.

## Table of Contents
1.  [Architecture Overview](#architecture-overview)
2.  [Microservices](#microservices)
3.  [Getting Started (Local Setup)](#getting-started-local-setup)
4.  [Running the Application](#running-the-application)
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

## Getting Started (Local Setup)

To run the application locally, you will need to install and run the following backing services.

### Prerequisites
-   **Java 17**
-   **Maven**
-   **MySQL**: Install and run a local MySQL server.
-   **Redis**: Install and run a local Redis server.
-   **Apache Kafka**: Download, install, and run a local Kafka server (with Zookeeper).

### Database Setup
Before starting the services, you must manually create the required database schemas in MySQL.
```sql
CREATE DATABASE auth_db;
CREATE DATABASE employee_db;
CREATE DATABASE attendance_db;
CREATE DATABASE report_db;
```

---

## Running the Application

**1. Build the Project**
First, build all the microservice JAR files using Maven.
```bash
mvn clean install
```

**2. Run the Services**
Start each microservice in the following order. You can run them from your IDE or by using the `mvn spring-boot:run` command in a separate terminal for each service.

1.  **Discovery Server**:
    ```bash
    cd discovery-server
    mvn spring-boot:run
    ```
2.  **API Gateway**:
    ```bash
    cd api-gateway
    mvn spring-boot:run
    ```
3.  **Auth Service**:
    ```bash
    cd auth-service
    mvn spring-boot:run
    ```
4.  **Employee Service**:
    ```bash
    cd employee-service
    mvn spring-boot:run
    ```
5.  **Attendance Service**:
    ```bash
    cd attendance-service
    mvn spring-boot:run
    ```
6.  **Report Service**:
    ```bash
    cd report-service
    mvn spring-boot:run
    ```
7.  **Notification Service**:
    ```bash
    cd notification-service
    mvn spring-boot:run
    ```

---

## Testing Guide: Data, URLs, and Roles

(This section remains the same as before)

---

## API Documentation (Swagger)

(This section remains the same as before)
