# SecureBank — Minimal Banking Application

SecureBank is a minimal banking application designed as a target application for the **Proactive Shift-Left Defense** hackathon track.

## Architecture

The project is structured as a monorepo containing:
- **Frontend (`frontend/securebank-ui`)**: React + TypeScript + Vite + Tailwind CSS dashboard.
- **Account Service (`services/account-service`)**: Spring Boot (Java 21) REST service connected to PostgreSQL `accounts_db` (Port 8081).
- **Transaction Service (`services/transaction-service`)**: Spring Boot (Java 21) REST service connected to PostgreSQL `transactions_db` (Port 8082).
- **Database Schemas (`database/`)**: Independent SQL schemas for `accounts_db` and `transactions_db`.
- **Docs (`docs/`)**: Architecture and security documentation.

## Prerequisites
- Java 21 (Oracle JDK / OpenJDK)
- Apache Maven 3.9+
- Node.js 18+ & npm
- PostgreSQL 16+ running on `localhost:5432` with user `postgres`

## Getting Started

### 1. Database Setup
Ensure PostgreSQL is running locally on port 5432.
Execute the database initialization scripts:
```bash
# Using psql:
psql -U postgres -h localhost -p 5432 -f database/accounts_db/schema.sql -d accounts_db
psql -U postgres -h localhost -p 5432 -f database/transactions_db/schema.sql -d transactions_db
```

### 2. Run Account Service (Port 8081)
```bash
cd services/account-service
mvn spring-boot:run
```

### 3. Run Transaction Service (Port 8082)
```bash
cd services/transaction-service
mvn spring-boot:run
```

### 4. Run Frontend (Port 5173)
```bash
cd frontend/securebank-ui
npm install
npm run dev
```
