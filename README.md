# SecureBank — Minimal Banking Application

SecureBank is a minimal, realistic banking application built as the target platform for a hackathon project under the track **Proactive Shift-Left Defense**.

The platform is designed to allow automated AI agents to inspect source code, detect security vulnerabilities (specifically SQL Injection), assess their severity, synthesize remediation patches, and verify code safety prior to deployment.

---

## 1. Monorepo Structure

```text
Bank_App_Hackathon/
│
├── frontend/
│   └── securebank-ui/             # React 18 + TypeScript + Vite + Tailwind CSS Dashboard
│
├── services/
│   ├── account-service/           # Java 21 + Spring Boot 3.3.5 (Port 8081)
│   └── transaction-service/       # Java 21 + Spring Boot 3.3.5 (Port 8082)
│
├── database/
│   ├── accounts_db/
│   │   └── schema.sql             # PostgreSQL schema for accounts
│   └── transactions_db/
│       └── schema.sql             # PostgreSQL schema for transactions
│
├── docs/
│   ├── architecture.md            # Architectural specification & decisions
│   ├── api-documentation.md       # Complete REST API reference
│   └── security-scenarios.md      # SQL Injection demonstration details & remediation
│
├── .gitignore
└── README.md
```

---

## 2. Architecture & Design Principles

1. **Microservice Autonomy**: Account Service and Transaction Service are completely independent services with zero cross-database dependencies or foreign keys.
2. **Dedicated Databases**:
   - `account-service` connects only to `accounts_db`.
   - `transaction-service` connects only to `transactions_db`.
3. **Direct SQL with `JdbcTemplate`**: Services use Spring JDBC and `JdbcTemplate` rather than Hibernate/JPA, providing full visibility and control over SQL execution.
4. **DTO Layered Separation**: `Controller` &rarr; `Service` &rarr; `Repository` architecture with input validation via Jakarta Bean Validation and centralized exception handling (`@RestControllerAdvice`).
5. **Controlled Security Demonstration Scenarios**:
   - **Scenario A (Account Service)**: Unparameterized query parameter concatenation in `GET /api/accounts/search?name=...`.
   - **Scenario B (Transaction Service)**: Dynamic WHERE parameter concatenation and unsafe dynamic `ORDER BY` clause injection in `GET /api/transactions/filter?...`.
   - Both scenarios include fully functional secure remediation targets documented in source code and in [`docs/security-scenarios.md`](docs/security-scenarios.md).

---

## 3. Technology Stack

- **Frontend**: React 18, TypeScript, Vite, React Router DOM, Tailwind CSS, Lucide React.
- **Backend**: Java 21, Spring Boot 3.3.5, Spring Web, Spring JDBC, PostgreSQL JDBC Driver, Jakarta Validation.
- **Database**: PostgreSQL 18.
- **Testing**: JUnit 5, Mockito, SpringBootTest, MockMvc.

---

## 4. Getting Started

### 4.1 Prerequisites
- Java 21 (Oracle JDK / OpenJDK)
- Apache Maven 3.9+
- Node.js 18+ and npm
- PostgreSQL running locally on `localhost:5432` with user `postgres`

### 4.2 Database Initialization
The databases and tables can be created via `psql` or pgAdmin using the schema files:
```bash
# Set password:
export PGPASSWORD=Mayank@7772   # (or $env:PGPASSWORD="Mayank@7772" on PowerShell)

# Create databases and apply schemas:
psql -U postgres -h localhost -p 5432 -c "CREATE DATABASE accounts_db;"
psql -U postgres -h localhost -p 5432 -c "CREATE DATABASE transactions_db;"
psql -U postgres -h localhost -p 5432 -d accounts_db -f database/accounts_db/schema.sql
psql -U postgres -h localhost -p 5432 -d transactions_db -f database/transactions_db/schema.sql
```

### 4.3 Running Account Service (Port 8081)
```bash
cd services/account-service
mvn spring-boot:run
```
*Health / API Base: `http://localhost:8081/api`*

### 4.4 Running Transaction Service (Port 8082)
```bash
cd services/transaction-service
mvn spring-boot:run
```
*Health / API Base: `http://localhost:8082/api`*

### 4.5 Running Frontend Dashboard (Port 5173)
```bash
cd frontend/securebank-ui
npm install
npm run dev
```
*Web Portal: `http://localhost:5173`*

---

## 5. Running the Test Suites

### Account Service Tests (15 tests)
```bash
cd services/account-service
mvn test
```

### Transaction Service Tests (14 tests)
```bash
cd services/transaction-service
mvn test
```

### Frontend Build & Typecheck
```bash
cd frontend/securebank-ui
npm run build
```

---

## 6. Seed Demo Data
Both backend services automatically seed realistic synthetic demonstration data on first startup:
- **Demo Users**:
  - `user-001` (Aarav Sharma) — Savings Account (`acc-1001`, balance: ₹25,000.00)
  - `user-002` (Priya Patel) — Savings Account (`acc-1002`, balance: ₹18,500.50) & Current Account (`acc-1003`, balance: ₹52,000.00)
  - `user-003` (Rohan Verma) — Savings Account (`acc-1004`, balance: ₹8,900.00)
  - `user-004` (Neha Gupta) — Frozen Account (`acc-1005`, balance: ₹45,000.00)
- **Demo Transactions**: 8 synthetic audit records with DEBIT/CREDIT types and unique reference IDs (`REF-20261103-001`, etc.).
