# Employee Attendance Management System

This is a full-stack employee attendance management system built with a Spring Boot microservices backend and a React frontend.

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway)
- Spring Security 6 with JWT
- MySQL 8
- Redis
- Apache Kafka
- Maven

### Frontend
- React 18
- Redux Toolkit
- React Router v6
- Tailwind CSS
- Shadcn UI
- Axios
- Vite

## Folder Structure

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
│   └── pom.xml
│
└── frontend/
    ├── src/
    ├── package.json
    └── ...
```

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker (for MySQL, Redis, Kafka)

### Backend Setup

1.  **Start Services:**
    ```bash
    docker-compose up -d mysql redis kafka
    ```
2.  **Build Project:**
    ```bash
    cd backend
    mvn clean install
    ```
3.  **Run Services (in separate terminals):**
    ```bash
    java -jar discovery-server/target/*.jar
    java -jar api-gateway/target/*.jar
    java -jar auth-service/target/*.jar
    java -jar employee-service/target/*.jar
    java -jar attendance-service/target/*.jar
    java -jar report-service/target/*.jar
    java -jar notification-service/target/*.jar
    ```

### Frontend Setup

1.  **Install Dependencies:**
    ```bash
    cd frontend
    npm install
    ```
2.  **Create `.env.local` file:**
    ```
    VITE_API_BASE_URL=http://localhost:8080/api
    ```
3.  **Run Development Server:**
    ```bash
    npm run dev
    ```

## Integration Diagram

```
[Frontend (React)] <--> [API Gateway] <--> [Discovery Server (Eureka)]
                             |
                             +--> [Auth Service]
                             +--> [Employee Service]
                             +--> [Attendance Service]
                             +--> [Report Service]
```

## API Mapping

| Frontend Route     | Backend Endpoint         | Service              |
| ------------------ | ------------------------ | -------------------- |
| `/login`           | `POST /api/auth/login`     | Auth Service         |
| `/register`        | `POST /api/auth/register`  | Auth Service         |
| `/admin/employees` | `GET /api/employee/all`  | Employee Service     |
| `/admin/employees` | `POST /api/employee`     | Employee Service     |
| `/admin/employees` | `PUT /api/employee/{id}` | Employee Service     |
| `/admin/employees` | `DELETE /api/employee/{id}`| Employee Service     |
| `/employee/checkin`| `POST /api/attendance/checkin`| Attendance Service   |
| `/employee/checkout`|`POST /api/attendance/checkout`| Attendance Service  |
| `/admin/reports`   | `GET /api/report`        | Report Service       |

## Role-Based Flow

- **ADMIN:** Can access `/admin/*` routes. Can manage employees and view reports.
- **EMPLOYEE:** Can access `/employee/*` routes. Can check in/out and view their own attendance.
- Unauthorized users are redirected to `/login`.

## Data Flow

1.  User logs in via the React app, which calls the Auth Service.
2.  Auth Service returns a JWT, which is stored in the browser.
3.  For subsequent requests, Axios interceptor attaches the JWT.
4.  API Gateway validates the JWT and routes the request.
5.  The appropriate microservice handles the request.
6.  Redux Toolkit manages the state in the frontend.

## Deployment

### Backend
- Build each service: `mvn clean install`
- Run each service with `java -jar ...`

### Frontend
- Build for production: `npm run build`
- Serve the `dist` folder with a static server.
