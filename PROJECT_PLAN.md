# Splitwise Clone - Detailed Project Plan

## Overview
A microservices-based expense splitting application built with Spring Boot, React, and MySQL. Each service will be independently deployable and maintainable.

## Architecture Overview

### Technology Stack
- **Backend**: Spring Boot 3.x
- **Frontend**: React 18+ with TypeScript
- **Database**: MySQL 8.0+
- **Service Discovery**: Eureka Server (or Consul)
- **API Gateway**: Spring Cloud Gateway
- **Message Queue**: RabbitMQ (for async communication)
- **Build Tool**: Maven
- **Containerization**: Docker (optional, for later)

### Microservices Breakdown

1. **User Service** - User management and authentication
2. **Group Service** - Group creation and management
3. **Expense Service** - Expense creation and splitting logic
4. **Settlement Service** - Debt settlement tracking
5. **Notification Service** - Email/push notifications
6. **API Gateway** - Single entry point for all client requests
7. **Service Discovery** - Eureka Server for service registration

---

## Phase 1: Foundation & Infrastructure

### Task 1.1: Project Structure Setup
- Create parent Maven project (multi-module)
- Set up common dependencies and configurations
- Create module structure for each microservice
- Set up React project structure

**Modules to create:**
```
splitwise/
├── splitwise-parent/          (Parent POM)
├── splitwise-common/          (Shared utilities, DTOs)
├── splitwise-discovery/       (Eureka Server)
├── splitwise-gateway/         (API Gateway)
├── splitwise-user-service/    (User Service)
├── splitwise-group-service/   (Group Service)
├── splitwise-expense-service/ (Expense Service)
├── splitwise-settlement-service/ (Settlement Service)
├── splitwise-notification-service/ (Notification Service)
└── splitwise-frontend/        (React App)
```

### Task 1.2: Service Discovery (Eureka Server)
- Set up Eureka Server
- Configure service registration
- Test service discovery

### Task 1.3: API Gateway Setup
- Configure Spring Cloud Gateway
- Set up routing rules
- Configure CORS
- Add request/response logging

### Task 1.4: Common Module
- Create shared DTOs (Data Transfer Objects)
- Create common exceptions
- Create utility classes
- Create common constants

---

## Phase 2: User Service

### Task 2.1: User Service - Database & Entity Setup
- Create MySQL database for user service
- Design User table schema
- Create User entity (JPA)
- Set up database connection

**User Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- username (VARCHAR, UNIQUE, NOT NULL)
- email (VARCHAR, UNIQUE, NOT NULL)
- password (VARCHAR, NOT NULL) - encrypted
- first_name (VARCHAR)
- last_name (VARCHAR)
- phone_number (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
- is_active (BOOLEAN)
```

### Task 2.2: User Service - Repository Layer
- Create UserRepository interface
- Add custom query methods
- Add pagination support

### Task 2.3: User Service - Service Layer
- User registration logic
- User login/authentication
- User profile update
- User search functionality
- Password encryption (BCrypt)

### Task 2.4: User Service - Controller Layer
- REST endpoints for user operations
- Input validation
- Error handling
- Response formatting

**Endpoints:**
- POST /api/users/register
- POST /api/users/login
- GET /api/users/{id}
- PUT /api/users/{id}
- GET /api/users/search?query={query}
- DELETE /api/users/{id}

### Task 2.5: User Service - Security
- JWT token generation
- Password hashing
- Input validation
- Security configurations

---

## Phase 3: Group Service

### Task 3.1: Group Service - Database & Entity Setup
- Create MySQL database for group service
- Design Group and GroupMember tables
- Create entities (JPA)
- Set up database connection

**Group Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- name (VARCHAR, NOT NULL)
- description (TEXT)
- created_by (BIGINT, FOREIGN KEY to User)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
- is_active (BOOLEAN)
```

**GroupMember Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- group_id (BIGINT, FOREIGN KEY to Group)
- user_id (BIGINT, FOREIGN KEY to User)
- role (VARCHAR) - ADMIN, MEMBER
- joined_at (TIMESTAMP)
- is_active (BOOLEAN)
```

### Task 3.2: Group Service - Repository Layer
- Create GroupRepository
- Create GroupMemberRepository
- Add custom queries

### Task 3.3: Group Service - Service Layer
- Create group
- Add members to group
- Remove members from group
- Get user's groups
- Get group details with members
- Update group information
- Delete group

### Task 3.4: Group Service - Controller Layer
- REST endpoints for group operations
- Integration with User Service (via Feign Client)

**Endpoints:**
- POST /api/groups
- GET /api/groups/{id}
- PUT /api/groups/{id}
- DELETE /api/groups/{id}
- GET /api/groups/user/{userId}
- POST /api/groups/{groupId}/members
- DELETE /api/groups/{groupId}/members/{userId}

### Task 3.5: Group Service - Inter-Service Communication
- Set up Feign Client to call User Service
- Handle service communication errors
- Add circuit breaker (Resilience4j)

---

## Phase 4: Expense Service

### Task 4.1: Expense Service - Database & Entity Setup
- Create MySQL database for expense service
- Design Expense and ExpenseParticipant tables
- Create entities (JPA)

**Expense Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- group_id (BIGINT, FOREIGN KEY to Group)
- paid_by (BIGINT, FOREIGN KEY to User)
- amount (DECIMAL, NOT NULL)
- description (TEXT)
- expense_date (DATE)
- split_type (VARCHAR) - EQUAL, UNEQUAL, PERCENTAGE, SHARES
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

**ExpenseParticipant Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- expense_id (BIGINT, FOREIGN KEY to Expense)
- user_id (BIGINT, FOREIGN KEY to User)
- amount_owed (DECIMAL, NOT NULL)
- amount_paid (DECIMAL, DEFAULT 0)
- created_at (TIMESTAMP)
```

### Task 4.2: Expense Service - Repository Layer
- Create ExpenseRepository
- Create ExpenseParticipantRepository
- Add complex queries for expense calculations

### Task 4.3: Expense Service - Service Layer
- Create expense with different split types:
  - Equal split
  - Unequal split (custom amounts)
  - Percentage split
  - Shares split
- Calculate individual shares
- Get expenses by group
- Get expenses by user
- Update expense
- Delete expense
- Calculate balances (who owes whom)

### Task 4.4: Expense Service - Controller Layer
- REST endpoints for expense operations
- Integration with Group and User Services

**Endpoints:**
- POST /api/expenses
- GET /api/expenses/{id}
- GET /api/expenses/group/{groupId}
- GET /api/expenses/user/{userId}
- PUT /api/expenses/{id}
- DELETE /api/expenses/{id}
- GET /api/expenses/balances/group/{groupId}
- GET /api/expenses/balances/user/{userId}

### Task 4.5: Expense Service - Business Logic
- Implement split calculation algorithms
- Balance calculation logic
- Debt simplification (minimize transactions)

---

## Phase 5: Settlement Service

### Task 5.1: Settlement Service - Database & Entity Setup
- Create MySQL database for settlement service
- Design Settlement table
- Create entity (JPA)

**Settlement Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- payer_id (BIGINT, FOREIGN KEY to User)
- payee_id (BIGINT, FOREIGN KEY to User)
- group_id (BIGINT, FOREIGN KEY to Group, nullable)
- amount (DECIMAL, NOT NULL)
- settlement_date (TIMESTAMP)
- status (VARCHAR) - PENDING, COMPLETED, CANCELLED
- notes (TEXT)
- created_at (TIMESTAMP)
```

### Task 5.2: Settlement Service - Repository Layer
- Create SettlementRepository
- Add queries for settlement history

### Task 5.3: Settlement Service - Service Layer
- Record settlement
- Get settlement history
- Get pending settlements for user
- Update settlement status
- Cancel settlement

### Task 5.4: Settlement Service - Controller Layer
- REST endpoints for settlement operations

**Endpoints:**
- POST /api/settlements
- GET /api/settlements/{id}
- GET /api/settlements/user/{userId}
- GET /api/settlements/group/{groupId}
- PUT /api/settlements/{id}
- DELETE /api/settlements/{id}

---

## Phase 6: Notification Service

### Task 6.1: Notification Service - Database & Entity Setup
- Create MySQL database for notification service
- Design Notification table
- Create entity (JPA)

**Notification Table Schema:**
```sql
- id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- user_id (BIGINT, FOREIGN KEY to User)
- type (VARCHAR) - EXPENSE_ADDED, SETTLEMENT, GROUP_INVITATION, etc.
- title (VARCHAR)
- message (TEXT)
- is_read (BOOLEAN, DEFAULT FALSE)
- created_at (TIMESTAMP)
```

### Task 6.2: Notification Service - Repository Layer
- Create NotificationRepository
- Add queries for unread notifications

### Task 6.3: Notification Service - Service Layer
- Create notification
- Mark as read
- Get user notifications
- Delete notification
- Send email notifications (optional, using JavaMailSender)

### Task 6.4: Notification Service - Controller Layer
- REST endpoints for notification operations

**Endpoints:**
- POST /api/notifications
- GET /api/notifications/user/{userId}
- PUT /api/notifications/{id}/read
- DELETE /api/notifications/{id}
- GET /api/notifications/user/{userId}/unread-count

### Task 6.5: Notification Service - Event-Driven Architecture
- Set up RabbitMQ
- Subscribe to events from other services
- Publish notifications based on events

---

## Phase 7: Frontend (React)

### Task 7.1: React Project Setup
- Initialize React app with TypeScript
- Set up routing (React Router)
- Set up state management (Redux Toolkit or Context API)
- Configure API client (Axios)
- Set up environment variables

### Task 7.2: Authentication Pages
- Login page
- Registration page
- Protected route wrapper
- JWT token management

### Task 7.3: Dashboard
- User dashboard
- Balance summary
- Recent activity
- Quick actions

### Task 7.4: Groups Management
- List all groups
- Create group page
- Group details page
- Add/remove members
- Group settings

### Task 7.5: Expense Management
- Add expense page (with split options)
- Expense list
- Expense details
- Edit/delete expense
- Balance view (who owes whom)

### Task 7.6: Settlement Management
- Settlement history
- Record settlement
- Pending settlements view

### Task 7.7: Notifications
- Notification center
- Real-time notifications (WebSocket or polling)
- Mark as read functionality

### Task 7.8: User Profile
- View profile
- Edit profile
- Change password

---

## Phase 8: Integration & Testing

### Task 8.1: Inter-Service Communication
- Set up Feign Clients for all service-to-service calls
- Add circuit breakers
- Add retry mechanisms
- Handle service failures gracefully

### Task 8.2: API Gateway Configuration
- Configure all routes
- Add authentication filter
- Add rate limiting
- Add request/response transformation

### Task 8.3: Error Handling
- Global exception handlers
- Standardized error responses
- Error logging

### Task 8.4: Testing
- Unit tests for services
- Integration tests
- API testing (Postman collection)

---

## Phase 9: Advanced Features (Optional)

### Task 9.1: Real-time Updates
- WebSocket integration
- Real-time balance updates
- Live notifications

### Task 9.2: Reporting
- Expense reports
- Spending analytics
- Export to CSV/PDF

### Task 9.3: Currency Support
- Multi-currency support
- Currency conversion

### Task 9.4: Recurring Expenses
- Set up recurring expenses
- Automatic expense creation

---

## Database Design Summary

Each microservice will have its own MySQL database:
- `user_db` - User Service
- `group_db` - Group Service
- `expense_db` - Expense Service
- `settlement_db` - Settlement Service
- `notification_db` - Notification Service

---

## Implementation Order Recommendation

1. **Phase 1**: Foundation (Discovery, Gateway, Common)
2. **Phase 2**: User Service (authentication is needed for other services)
3. **Phase 3**: Group Service
4. **Phase 4**: Expense Service (core functionality)
5. **Phase 5**: Settlement Service
6. **Phase 6**: Notification Service
7. **Phase 7**: Frontend (can start after User Service is ready)
8. **Phase 8**: Integration & Testing
9. **Phase 9**: Advanced Features (optional)

---

## Key Design Decisions

1. **Database per Service**: Each microservice has its own database
2. **Synchronous Communication**: Feign Client for direct service calls
3. **Asynchronous Communication**: RabbitMQ for event-driven notifications
4. **API Gateway**: Single entry point for all client requests
5. **Service Discovery**: Eureka for dynamic service registration
6. **Security**: JWT tokens for authentication
7. **Error Handling**: Centralized error handling with standard responses

---

## Next Steps

Once you're ready, you can start implementing by saying:
- "Start with Task 1.1: Project Structure Setup"
- "Implement Task 2.1: User Service Database Setup"
- etc.

Each task will be implemented independently so you can learn step by step!

