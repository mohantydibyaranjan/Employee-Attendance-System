# Employee Attendance System

This is a complete Spring Boot Microservices Backend for an Employee Attendance System.

## Architecture

The system is built using a microservices architecture, with the following components:

*   **Spring Boot 3:** The core framework for building the microservices.
*   **Spring Cloud:** Provides tools for building and managing microservices, including a service discovery server and an API gateway.
*   **Spring Security (JWT):** Used for securing the application with JSON Web Tokens.
*   **Kafka:** A distributed streaming platform used for real-time event processing.
*   **Redis:** An in-memory data store used for caching online employee data.
*   **MySQL:** A relational database used for data persistence.
*   **Docker Compose:** Used for orchestrating the deployment of all the services.

## Microservices

The system is composed of the following microservices:

*   **Discovery Server:** A Eureka server that acts as a service registry, allowing services to dynamically discover and communicate with each other.
*   **API Gateway:** A Spring Cloud Gateway that serves as the single entry point for all client requests, handling routing and centralizing security.
*   **Auth Service:** Responsible for user registration, login, and JWT token generation.
*   **Employee Service:** Handles all CRUD operations related to employee data.
*   **Attendance Service:** Manages employee check-ins and check-outs, persists attendance data to a MySQL database, and publishes events to a Kafka topic.
*   **Report Service:** Subscribes to the Kafka topic to consume attendance events in real-time, processes the data to generate daily and monthly attendance summaries, and exposes endpoints for querying these reports.
*   **Notification Service:** Subscribes to the Kafka topic to detect events that require notifications, such as late check-ins.

## How to Build and Run

To build and run the entire system, you will need to have Docker and Docker Compose installed.

1.  Clone the repository:

    ```bash
    git clone https://github.com/your-username/employee-attendance-system.git
    ```

2.  Navigate to the project directory:

    ```bash
    cd employee-attendance-system
    ```

3.  Build the project using Maven:

    ```bash
    mvn clean install
    ```

4.  Run the system using Docker Compose:

    ```bash
    docker-compose up --build
    ```

This will build and run all the services, including the microservices, Kafka, Redis, and MySQL.

## API Endpoints

The following are the main API endpoints exposed by the system:

*   **/auth/register:** Registers a new user.
*   **/auth/login:** Authenticates an existing user and returns a JWT.
*   **/employee:** Creates a new employee.
*   **/employee/{id}:** Retrieves an employee by their ID.
*   **/employee/{id}:** Updates an employee's data.
*   **/employee/{id}:** Deletes an employee.
*   **/employee/all:** Retrieves all employees.
*   **/attendance/checkin:** Records an employee's check-in.
*   **/attendance/checkout:** Records an employee's check-out.
*   **/attendance/today/{employeeId}:** Retrieves today's attendance for a given employee.
*   **/attendance/history/{employeeId}:** Retrieves the attendance history for a given employee.
*   **/report/summary/monthly:** Retrieves the monthly attendance summary for a given employee.
*   **/report/summary/daily:** Retrieves the daily attendance summary.
