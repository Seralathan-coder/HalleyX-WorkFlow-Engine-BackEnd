# ⚡ Workflow Engine — Backend (Spring Boot)

> RESTful API server for dynamic workflow management — built with Spring Boot 3, Java 17, JPA/Hibernate, and MySQL 8.

-----------------------------------------------------------------
                                                                
                                                                 
 PROJECT DEMO  YOUTUBE LINK:https://youtu.be/OIMN_xEhpy0       
------------------------------------------------------------------
## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [API Reference](#api-reference)
- [Key Features](#key-features)
- [Design Patterns](#design-patterns)

---

## Overview

The Workflow Engine backend is a **Spring Boot REST API** that enables users to:

- **Define** workflows with customizable input schemas
- **Configure** steps (task, approval, notification) and routing rules
- **Execute** workflows with real-time progress tracking
- **Manage** executions (approve, reject, cancel, retry)
- **Audit** all workflow executions with timestamped logs

The backend evaluates routing rules using **Spring Expression Language (SpEL)** to dynamically determine workflow flow at runtime.

---

## Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17+ | Primary language |
| **Spring Boot** | 3.2.3 | Application framework with embedded Tomcat |
| **Spring Web MVC** | — | REST controllers (`@GetMapping`, `@PostMapping`, etc.) |
| **Spring Data JPA** | — | Data access layer with derived query methods |
| **Hibernate** | 6.x | ORM — maps Java entities to MySQL tables |
| **MySQL** | 8.x | Relational database |
| **SpEL** | — | Rule engine for dynamic condition evaluation |
| **Jackson** | — | JSON serialization/deserialization |
| **Lombok** | — | Reduces boilerplate (getters, setters, builders) |
| **Maven** | 3.x | Build & dependency management |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│               Spring Boot Application               │
│                    (Port 8081)                        │
│                                                      │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────┐│
│   │ Controllers  │───▶│  Services   │───▶│  Repos  ││
│   │ (REST API)   │    │ (Business   │    │ (JPA)   ││
│   │              │    │  Logic)     │    │         ││
│   └─────────────┘    └──────┬──────┘    └────┬────┘│
│                             │                 │     │
│                   ┌─────────┴──────┐          │     │
│                   │  Rule Engine   │          │     │
│                   │  (SpEL Parser) │          │     │
│                   └────────────────┘          │     │
└──────────────────────────────────────────────┘     │
                                        │             │
                                        ▼             │
                             ┌────────────────┐       │
                             │   MySQL 8      │◀──────┘
                             │ workflow_engine │
                             └────────────────┘
```

**Layer Responsibilities:**

| Layer | Package | Role |
|-------|---------|------|
| **Presentation** | `controller/` | Receives HTTP requests, returns JSON responses |
| **Business Logic** | `service/` | Workflow CRUD, execution engine, rule evaluation |
| **Data Access** | `repository/` | Spring Data JPA interfaces (auto-generated SQL) |
| **Domain** | `model/` | JPA entities mapped to database tables |
| **Transfer** | `dto/` | Data Transfer Objects for API request/response |
| **Config** | `config/` | CORS, data seeder |
| **Error Handling** | `exception/` | Global exception handler (`@ControllerAdvice`) |

---

## Project Structure

```
workflow-engine/
├── pom.xml                                    ← Maven build config
├── src/main/
│   ├── java/com/workflow/
│   │   ├── WorkflowEngineApplication.java     ← @SpringBootApplication entry point
│   │   ├── config/
│   │   │   ├── WebConfig.java                 ← CORS: allows localhost:5173, :3000
│   │   │   └── DataInitializer.java           ← Seeds sample workflows on first run
│   │   ├── controller/
│   │   │   ├── WorkflowController.java        ← /api/workflows
│   │   │   ├── StepController.java            ← /api/steps, /api/workflows/{id}/steps
│   │   │   ├── RuleController.java            ← /api/rules, /api/steps/{id}/rules
│   │   │   └── ExecutionController.java       ← /api/executions, execute/approve/reject
│   │   ├── dto/
│   │   │   ├── WorkflowDTO.java
│   │   │   ├── StepDTO.java
│   │   │   ├── RuleDTO.java
│   │   │   └── ExecutionDTO.java
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java    ← Maps exceptions to HTTP status codes
│   │   ├── model/
│   │   │   ├── Workflow.java                  ← workflows table entity
│   │   │   ├── Step.java                      ← steps table entity (task/approval/notification)
│   │   │   ├── Rule.java                      ← rules table entity (SpEL conditions)
│   │   │   └── Execution.java                 ← executions table entity
│   │   ├── repository/
│   │   │   ├── WorkflowRepository.java
│   │   │   ├── StepRepository.java
│   │   │   ├── RuleRepository.java
│   │   │   └── ExecutionRepository.java
│   │   └── service/
│   │       ├── WorkflowService.java           ← CRUD + versioning + soft delete
│   │       ├── ExecutionService.java           ← Execution engine (start/advance/approve)
│   │       ├── RuleEngineService.java          ← SpEL expression evaluation
│   │       └── NotificationService.java        ← Notification step handler
│   └── resources/
│       ├── application.yml                     ← Server port, DB connection, JPA config
│       └── schema.sql                          ← Database table definitions
```

---

## Prerequisites

| Requirement | Version |
|------------|---------|
| **Java JDK** | 17 or higher |
| **Maven** | 3.6+ (or use included `mvnw` wrapper) |
| **MySQL** | 8.0+ |

---

## Database Setup

1. **Start MySQL** and ensure it's running on port `3306`

2. **Create the database** (auto-created if `createDatabaseIfNotExist=true` is in the URL):
   ```sql
   CREATE DATABASE IF NOT EXISTS workflow_engine;
   ```

3. **Tables are created automatically** from `schema.sql` on every startup (`spring.sql.init.mode=always`)

### Database Schema (4 tables)

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `workflows` | Workflow definitions | `id`, `name`, `version`, `is_active`, `input_schema`, `start_step_id` |
| `steps` | Steps within workflows | `id`, `workflow_id`, `name`, `step_type`, `step_order`, `metadata` |
| `rules` | Routing rules between steps | `id`, `step_id`, `rule_condition`, `next_step_id`, `priority` |
| `executions` | Runtime execution records | `id`, `workflow_id`, `status`, `data`, `logs`, `current_step_id` |

---

## Configuration

**`application.yml`:**
```yaml
server:
  port: 8081                          # Backend runs on port 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflow_engine?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: user                    # ← Change to your MySQL password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none                  # We use schema.sql instead
    show-sql: true                    # Logs SQL queries to console

  sql:
    init:
      mode: always                    # Runs schema.sql every startup
      schema-locations: classpath:schema.sql
```

> **Note:** Update `username` and `password` to match your MySQL credentials.

---

## How to Run

```bash
# Option 1: Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Option 2: Using installed Maven
mvn spring-boot:run

# Option 3: Build and run JAR
mvn clean package -DskipTests
java -jar target/workflow-engine-1.0.0.jar
```

The server starts at **http://localhost:8081**

### Verify

```bash
curl http://localhost:8081/api/workflows
```

---

## API Reference

### Workflows

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/workflows` | List workflows (paginated, supports `?name=`, `?isActive=`, `?page=`, `?size=`) |
| `GET` | `/api/workflows/{id}` | Get single workflow by ID |
| `POST` | `/api/workflows` | Create new workflow |
| `PUT` | `/api/workflows/{id}` | Update workflow (creates new version) |
| `DELETE` | `/api/workflows/{id}` | Soft-delete workflow (`is_active = false`) |

### Steps

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/workflows/{id}/steps` | List steps for a workflow |
| `POST` | `/api/workflows/{id}/steps` | Add step to workflow |
| `PUT` | `/api/steps/{id}` | Update step |
| `DELETE` | `/api/steps/{id}` | Delete step |

### Rules

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/steps/{id}/rules` | List rules for a step |
| `POST` | `/api/steps/{id}/rules` | Add rule to step |
| `PUT` | `/api/rules/{id}` | Update rule |
| `DELETE` | `/api/rules/{id}` | Delete rule |

### Executions

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/workflows/{id}/execute` | Start workflow execution |
| `GET` | `/api/workflows/{id}/executions` | List executions for a workflow |
| `GET` | `/api/executions` | List all executions (paginated) |
| `GET` | `/api/executions/{id}` | Get single execution |
| `POST` | `/api/executions/{id}/approve` | Approve current approval step |
| `POST` | `/api/executions/{id}/reject` | Reject current approval step |
| `POST` | `/api/executions/{id}/cancel` | Cancel running execution |
| `POST` | `/api/executions/{id}/retry` | Retry failed execution |

---

## Key Features

| Feature | Description |
|---------|-------------|
| **Dynamic Workflow Definition** | Workflows defined at runtime via API — not hardcoded |
| **3 Step Types** | `task` (auto-execute), `approval` (human-in-the-loop), `notification` |
| **SpEL Rule Engine** | Evaluate conditions like `amount > 1000 && priority == 'High'` |
| **Versioning** | Each workflow update creates a new version; old executions reference old versions |
| **Soft Delete** | Workflows are deactivated, not destroyed; preserves audit trail |
| **Execution Logging** | Every step logs timestamped messages as a JSON array |
| **Error Handling** | Global `@ControllerAdvice` maps exceptions to proper HTTP status codes |
| **CORS** | Pre-configured for frontend origins (`localhost:5173`, `localhost:3000`) |
| **Data Seeding** | `DataInitializer` seeds sample workflows on first run |
| **Pagination** | All list endpoints support Spring Data pagination |

---

## Design Patterns

| Pattern | Implementation |
|---------|---------------|
| **Layered Architecture** | Controller → Service → Repository |
| **DTO Pattern** | Entities ≠ API responses; DTOs add computed fields |
| **Repository Pattern** | Spring Data JPA interfaces with derived query methods |
| **Builder Pattern** | Manually implemented builders on all entities |
| **Strategy Pattern** | Different execution behavior per step type |
| **Soft Delete** | `is_active = false` instead of SQL DELETE |
| **Versioning** | Old workflow deactivated, new version created |
| **Global Exception Handler** | `@ControllerAdvice` with exception-to-status mapping |

---

## Related

- **Frontend:** See [`../workflow-frontend/README.md`](../workflow-frontend/README.md) for the React UI
- **Full Report:** See [`../WorkflowEngine_Project_Report.md`](../WorkflowEngine_Project_Report.md) for complete documentation
