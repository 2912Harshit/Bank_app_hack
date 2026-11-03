# SecureBank — Architecture Overview

## 1. Introduction
SecureBank is a minimal, realistic banking application built as a target platform for security analysis and verification in the "Proactive Shift-Left Defense" hackathon track.

## 2. Architecture Principles
- **Monorepo Layout**: Contains both microservices (`account-service`, `transaction-service`), the React frontend (`securebank-ui`), database schemas, and documentation.
- **Microservice Autonomy**: Account Service and Transaction Service are independently deployable and each owns its dedicated PostgreSQL database (`accounts_db` and `transactions_db`).
- **Database Isolation**: Services never access each other's databases directly. There are no cross-database foreign keys.
- **Spring JDBC & JdbcTemplate**: The application uses Spring JDBC with `JdbcTemplate` rather than Spring Data JPA/Hibernate, providing explicit control over SQL queries and data mapping.
- **Layered DTO Architecture**: Controllers handle HTTP concerns and validation; Services encapsulate business logic; Repositories manage database access with RowMappers.
- **Controlled Security Scenarios**: Controlled, isolated SQL injection scenarios are documented and isolated for automated vulnerability detection and remediation demonstrations.

## 3. Technology Stack
- **Frontend**: React 18 / TypeScript / Vite / React Router / Tailwind CSS
- **Backend Services**: Java 21, Spring Boot 3.4.x, Spring Web, Spring JDBC, PostgreSQL JDBC Driver, Jakarta Validation
- **Databases**: PostgreSQL 18 (`accounts_db`, `transactions_db`)
- **Port Allocation**:
  - Account Service: `http://localhost:8081`
  - Transaction Service: `http://localhost:8082`
  - Frontend UI: `http://localhost:5173`
