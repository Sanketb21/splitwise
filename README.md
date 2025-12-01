# Splitwise Clone - Microservices Application

A learning project to build a Splitwise clone using Spring Boot microservices, React frontend, and MySQL.

## Project Structure

This is a multi-module Maven project with the following modules:

- `splitwise-common` - Shared utilities, DTOs, and common components
- `splitwise-discovery` - Eureka Server for service discovery
- `splitwise-gateway` - API Gateway (Spring Cloud Gateway)
- `splitwise-user-service` - User management and authentication
- `splitwise-group-service` - Group management
- `splitwise-expense-service` - Expense management and splitting
- `splitwise-settlement-service` - Settlement tracking
- `splitwise-notification-service` - Notification management
- `splitwise-frontend` - React frontend application

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+ and npm (for frontend)
- RabbitMQ (for notifications, optional in early phases)

## Getting Started

This project is being built step by step. See `PROJECT_PLAN.md` for the detailed implementation plan.

## Current Status

### Phase 1: Foundation & Infrastructure ✅ COMPLETED
- ✅ Task 1.1: Project Structure Setup
- ✅ Task 1.2: Service Discovery (Eureka Server)
- ✅ Task 1.3: API Gateway Setup
- ✅ Task 1.4: Common Module

### Phase 2: User Service ✅ COMPLETED
- ✅ Task 2.1: Database & Entity Setup
- ✅ Task 2.2: Repository Layer
- ✅ Task 2.3: Service Layer
- ✅ Task 2.4: Controller Layer
- ✅ Task 2.5: Security (JWT, BCrypt)

### Phase 3: Group Service ✅ COMPLETED
- ✅ Task 3.1: Database & Entity Setup
- ✅ Task 3.2: Repository Layer
- ✅ Task 3.3: Service Layer
- ✅ Task 3.4: Controller Layer
- ✅ Task 3.5: Inter-Service Communication (Feign Client + Circuit Breaker)

### Completed Modules:
- `splitwise-common` - Shared DTOs, exceptions, utilities, and constants
- `splitwise-discovery` - Eureka Server running on port 8761
- `splitwise-gateway` - API Gateway running on port 8080
- `splitwise-user-service` - User management and authentication (port 8081)
- `splitwise-group-service` - Group management with inter-service communication (port 8082)

## Running the Services

### 1. Discovery Service (Eureka Server)
To start the Eureka Discovery Service:

```bash
cd splitwise-discovery
mvn spring-boot:run
```

The Eureka Server dashboard will be available at: http://localhost:8761

**Note:** Start the Discovery Service first, as other services need to register with it.

### 2. API Gateway
To start the API Gateway:

```bash
cd splitwise-gateway
mvn spring-boot:run
```

The API Gateway will be available at: http://localhost:8080

**Note:** Start the Discovery Service before starting the Gateway, as the Gateway needs to connect to Eureka.

### 3. User Service
To start the User Service:

```bash
cd splitwise-user-service
mvn spring-boot:run
```

The User Service will be available at: http://localhost:8081

**Note:** Ensure MySQL is running and the `user_db` database is created (or will be auto-created).

### 4. Group Service
To start the Group Service:

```bash
cd splitwise-group-service
mvn spring-boot:run
```

The Group Service will be available at: http://localhost:8082

**Note:** Ensure MySQL is running and the `group_db` database is created (or will be auto-created). The Group Service communicates with the User Service via Feign Client.

## Service Startup Order

1. **Discovery Service** (port 8761) - Start first
2. **User Service** (port 8081) - Can start after Discovery
3. **Group Service** (port 8082) - Requires Discovery and User Service
4. **API Gateway** (port 8080) - Can start after Discovery (optional but recommended)

## Testing the Services

### User Service Endpoints
- Register: `POST http://localhost:8081/api/users/register`
- Login: `POST http://localhost:8081/api/users/login`
- Get User: `GET http://localhost:8081/api/users/{id}`
- Search Users: `GET http://localhost:8081/api/users/search?query={query}`

### Group Service Endpoints
- Create Group: `POST http://localhost:8082/api/groups?createdBy={userId}`
- Get Group: `GET http://localhost:8082/api/groups/{id}`
- List Groups: `GET http://localhost:8082/api/groups?page=0&size=10`
- Add Member: `POST http://localhost:8082/api/groups/{groupId}/members?addedBy={userId}`
- Update Member Role: `PUT http://localhost:8082/api/groups/{groupId}/members/{userId}/role?role=ADMIN&updatedBy={userId}`

### Via API Gateway
All endpoints are also accessible through the API Gateway at `http://localhost:8080/api/{service-path}`

## Features Implemented

### User Service
- User registration and authentication
- JWT token-based authentication
- Password encryption with BCrypt
- User search with pagination
- User profile management

### Group Service
- Group creation and management
- Member management (add/remove/update role)
- Group search and pagination
- Inter-service communication with User Service
- Circuit breaker pattern for resilience
- Comprehensive error handling

## Next Steps

The next phase is **Phase 4: Expense Service**. Follow the tasks in `PROJECT_PLAN.md` to continue building the application incrementally.

