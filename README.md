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

✅ Task 1.1: Project Structure Setup - COMPLETED  
✅ Task 1.2: Service Discovery (Eureka Server) - COMPLETED  
✅ Task 1.3: API Gateway Setup - COMPLETED  
✅ Task 1.4: Common Module - COMPLETED

### Completed Modules:
- `splitwise-common` - Shared DTOs, exceptions, utilities, and constants
- `splitwise-discovery` - Eureka Server running on port 8761
- `splitwise-gateway` - API Gateway running on port 8080

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

## Next Steps

Follow the tasks in `PROJECT_PLAN.md` one by one to build the application incrementally.

