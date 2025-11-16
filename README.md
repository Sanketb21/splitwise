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

### Completed Modules:
- `splitwise-discovery` - Eureka Server running on port 8761

## Running the Discovery Service

To start the Eureka Discovery Service:

```bash
cd splitwise-discovery
mvn spring-boot:run
```

The Eureka Server dashboard will be available at: http://localhost:8761

## Next Steps

Follow the tasks in `PROJECT_PLAN.md` one by one to build the application incrementally.

