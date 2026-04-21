# Complaint & Feedback Management System

**OOAD Mini-Project — UE23CS352B | PES University**

A web-based system for managing customer complaints and feedback, built with **Spring Boot MVC**, **Thymeleaf**, **MySQL**, and **Spring Security**.

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Setup
```bash
# 1. Create MySQL database (auto-created if config allows)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS complaint_feedback_db;"

# 2. Update credentials in src/main/resources/application.properties
#    spring.datasource.username=root
#    spring.datasource.password=root

# 3. Run the application
mvn spring-boot:run

# 4. Open browser: http://localhost:8080
```

### Default Accounts
| Role           | Username | Password  |
|---------------|----------|-----------|
| Admin          | admin    | admin123  |
| Support Staff  | staff1   | staff123  |
| Support Staff  | staff2   | staff123  |
| Customer       | (register via /register) |  |

---

## Architecture: MVC (Model-View-Controller)

```
┌──────────────────────────────────────────────────────────┐
│                    SPRING BOOT MVC                        │
├──────────┬───────────────────┬───────────────────────────┤
│  VIEW    │   CONTROLLER      │   MODEL                   │
│ Thymeleaf│   @Controller     │   @Entity + @Service      │
│ templates│   AuthController  │   User, Complaint,        │
│ (HTML)   │   CustomerCtrl    │   Feedback, Notification  │
│          │   AdminController │   + JPA Repositories      │
│          │   StaffController │   + Service layer         │
└──────────┴───────────────────┴───────────────────────────┘
```

- **Model**: JPA entities (User, Complaint, Feedback, Notification) + Service layer
- **View**: Thymeleaf HTML templates with shared fragments
- **Controller**: Spring MVC controllers routing requests to services

---

## Design Patterns (4 total)

### 1. Factory Method Pattern (Creational)
**File**: `pattern/factory/NotificationFactory.java`

Creates notification objects based on complaint lifecycle events. The factory encapsulates construction logic so services don't need to know how to build different notification types.

```
NotificationFactory.createComplaintSubmitted(complaint, customer)
NotificationFactory.createComplaintAssigned(complaint, staff)
NotificationFactory.createComplaintResolved(complaint, customer)
NotificationFactory.createComplaintEscalated(complaint, admin)
```

### 2. Decorator Pattern (Structural)
**Files**: `pattern/decorator/*.java`

Dynamically adds metadata to complaint descriptions at display time without modifying the Complaint entity. Decorators can be stacked:
```
ComplaintDisplay display = new BasicComplaintDisplay(complaint);
display = new PriorityDecorator(display, complaint.getPriority());
display = new CategoryDecorator(display, complaint.getCategory());
display = new TimestampDecorator(display, complaint.getCreatedAt());
// Output: [INFRASTRUCTURE] [🟡 MEDIUM] Wifi not working... [Filed: 12-Apr-2026 10:30]
```

### 3. Observer Pattern (Behavioral)
**Files**: `pattern/observer/ComplaintObserver.java`, `pattern/observer/NotificationObserver.java`

Decouples complaint state changes from side effects. When a complaint is submitted, assigned, resolved, etc., all registered observers are notified automatically:
```
for (ComplaintObserver observer : observers) {
    observer.onComplaintSubmitted(complaint);
}
```

### 4. Strategy Pattern (Behavioral — bonus)
**Files**: `pattern/strategy/AssignmentStrategy.java`, `RoundRobinStrategy.java`, `LoadBalancedStrategy.java`

Defines interchangeable algorithms for assigning complaints to staff. The admin can switch strategies without modifying the complaint service:
- **RoundRobinStrategy**: Assigns in sequence
- **LoadBalancedStrategy**: Assigns to staff with fewest active complaints

### 5. MVC Pattern (Framework-enforced)
Spring Boot's `@Controller` + Thymeleaf + `@Service` + `@Entity` layers naturally enforce MVC.

---

## Design Principles (4 total)

### 1. SRP — Single Responsibility Principle
Each class has one reason to change:
- `NotificationFactory` only handles notification construction
- `ComplaintServiceImpl` only handles complaint business logic
- Each controller handles only its role's endpoints

### 2. OCP — Open/Closed Principle
- New notification types → add factory methods, don't modify existing ones
- New decorators → create new decorator class, don't modify existing chain
- New observers → implement `ComplaintObserver`, auto-discovered by Spring

### 3. DIP — Dependency Inversion Principle
- Controllers depend on service **interfaces** (`ComplaintService`), not implementations
- `ComplaintServiceImpl` depends on `AssignmentStrategy` interface, not concrete strategies
- `ComplaintServiceImpl` depends on `ComplaintObserver` interface, not `NotificationObserver`

### 4. LSP — Liskov Substitution Principle
- All `ComplaintDisplay` decorators are substitutable for the base `ComplaintDisplay` interface
- `RoundRobinStrategy` and `LoadBalancedStrategy` are interchangeable via the `AssignmentStrategy` interface
- Any `ComplaintObserver` implementation can be swapped without breaking the complaint service

---

## State Diagrams Implemented

### Complaint State Diagram
```
Open → Assigned → InProgress → Resolved → Closed
                  InProgress → Escalated
                  Resolved → Reopened → InProgress (reprocess)
                  InProgress ↺ addResponse (self-loop)
```

### User Account State Diagram
```
Registered → Active ↺ login/logout
Active → Suspended (violationDetected)
Suspended → Active (reinstated)
Active → Deactivated (deleteAccount) [final]
```

### Feedback State Diagram
```
Draft → Submitted → UnderReview → Approved (stored)
                                → Rejected (discarded)
```

### Notification State Diagram
```
Created → Sent → Delivered → Read → Archived
                           → Unread → Archived
               → Failed
```

---

## Use Cases (4 Major + 4 Minor)

### Major Use Cases
| # | Use Case | Actor | Owner |
|---|----------|-------|-------|
| 1 | Submit Complaint | Customer | Member 1 |
| 2 | Manage & Assign Complaints | Admin | Member 2 |
| 3 | Resolve Complaint (View Assigned, Start Work, Add Response, Resolve) | Support Staff | Member 3 |
| 4 | Track Complaint & Give Feedback | Customer | Member 4 |

### Minor Use Cases
| # | Use Case | Actor |
|---|----------|-------|
| 1 | Register / Login | All |
| 2 | View Notifications | All |
| 3 | Generate Reports | Admin |
| 4 | Manage Users (Create Staff, Suspend, Deactivate) | Admin |

---

## Project Structure

```
complaint-feedback-system/
├── pom.xml
├── src/main/java/com/pesu/cfs/
│   ├── ComplaintFeedbackApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── CustomUserDetailsService.java
│   │   └── DataInitializer.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── CustomerController.java
│   │   ├── AdminController.java
│   │   └── StaffController.java
│   ├── dto/
│   │   ├── RegistrationDto.java
│   │   ├── ComplaintDto.java
│   │   └── FeedbackDto.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Complaint.java
│   │   ├── Feedback.java
│   │   ├── Notification.java
│   │   └── enums/ (Role, AccountStatus, ComplaintStatus, ...)
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ComplaintRepository.java
│   │   ├── FeedbackRepository.java
│   │   └── NotificationRepository.java
│   ├── service/
│   │   ├── UserService.java, ComplaintService.java, ...
│   │   └── impl/ (implementations)
│   └── pattern/
│       ├── factory/NotificationFactory.java
│       ├── observer/ComplaintObserver.java, NotificationObserver.java
│       ├── decorator/ComplaintDisplay.java, PriorityDecorator.java, ...
│       └── strategy/AssignmentStrategy.java, RoundRobinStrategy.java, ...
├── src/main/resources/
│   ├── application.properties
│   ├── static/css/style.css
│   └── templates/
│       ├── fragments/layout.html
│       ├── auth/login.html, register.html
│       ├── customer/dashboard.html, complaint-form.html, ...
│       ├── admin/dashboard.html, complaints.html, users.html, ...
│       └── staff/dashboard.html, complaints.html, complaint-detail.html
└── .gitignore
```

---

## Tech Stack
- **Backend**: Java 17, Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, HTML/CSS
- **Database**: MySQL 8.0
- **Build**: Maven
