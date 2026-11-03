# SecureBank — Controlled SQL Injection Demonstration Scenarios

## 1. Context & Purpose
SecureBank serves as the target application for the **Proactive Shift-Left Defense** hackathon track.
Under this track, automated multi-agent security tooling analyzes source code, traces untrusted data flows, detects vulnerabilities, assesses severity, and synthesizes verifiable remediation patches prior to build and deployment.

To evaluate detection and patching capabilities accurately, two distinct and controlled SQL Injection (CWE-89) scenarios have been intentionally introduced.

---

## 2. Scenario A — Unparameterized Value Concatenation (Account Service)

### Endpoint
`GET /api/accounts/search?name={name}`

### Vulnerability Details
- **Classification**: CWE-89: Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')
- **File**: `services/account-service/src/main/java/com/securebank/accounts/repository/AccountRepositoryImpl.java`
- **Method**: `searchByCustomerName(String name)`

### Data Flow (Source to Sink)
1. **Source**: HTTP Query parameter `name` received in `AccountController.searchAccounts(@RequestParam String name)`.
2. **Transfer**: Forwarded without sanitization to `AccountServiceImpl.searchAccountsByName(name)`.
3. **Sink**: Appended directly into the SQL string literal inside `AccountRepositoryImpl.searchByCustomerName(name)`:
   ```java
   String sql = "SELECT * FROM accounts WHERE customer_name = '" + name + "'";
   return jdbcTemplate.query(sql, rowMapper);
   ```

### Demonstration Payload (Safe, Read-Only Verification)
- **Normal Request**:
  `GET /api/accounts/search?name=Aarav%20Sharma`
  - Result: Returns single matching account for Aarav Sharma.
- **Controlled Injection Payload**:
  `GET /api/accounts/search?name=' OR '1'='1`
  - SQL Executed: `SELECT * FROM accounts WHERE customer_name = '' OR '1'='1'`
  - Result: Evaluates to `TRUE` for all rows, returning the full dataset regardless of name.

### Secure Remediation Target
Parameterize the query with `JdbcTemplate` argument binding so the database driver strictly interprets input as a literal data value:
```java
// Method: searchByCustomerNameSecure(String name)
String sql = "SELECT * FROM accounts WHERE LOWER(customer_name) LIKE LOWER(?) ORDER BY customer_name ASC";
return jdbcTemplate.query(sql, rowMapper, "%" + name.trim() + "%");
```

---

## 3. Scenario B — Dynamic Query Building & ORDER BY Injection (Transaction Service)

### Endpoint
`GET /api/transactions/filter?accountId={accountId}&sortBy={sortBy}&sortDirection={sortDirection}`

### Vulnerability Details
- **Classification**: CWE-89: SQL Injection (Value Concatenation & Identifier Clause Injection)
- **File**: `services/transaction-service/src/main/java/com/securebank/transactions/repository/TransactionRepositoryImpl.java`
- **Method**: `filter(String accountId, ... String sortBy, String sortDirection)`

### Data Flow (Source to Sink)
1. **Source**: HTTP Query parameters `accountId`, `sortBy`, `sortDirection` received in `TransactionController.filterTransactions(...)`.
2. **Transfer**: Forwarded to `TransactionServiceImpl.filterTransactions(...)`.
3. **Sink 1 (WHERE Clause)**:
   ```java
   if (accountId != null && !accountId.isBlank()) {
       sql.append(" AND account_id = '").append(accountId).append("'");
   }
   ```
4. **Sink 2 (ORDER BY Clause)**:
   SQL identifiers (table and column names) cannot be bound with PreparedStatement `?` placeholders. Directly appending untrusted strings creates an identifier injection flaw:
   ```java
   sql.append(" ORDER BY ").append(orderField).append(" ").append(orderDir);
   ```

### Demonstration Payload (Safe, Read-Only Verification)
- **Normal Request**:
  `GET /api/transactions/filter?accountId=acc-1001&sortBy=created_at&sortDirection=DESC`
  - Result: Filters and sorts transactions cleanly.
- **Controlled Value Injection**:
  `GET /api/transactions/filter?accountId=acc-1001' OR '1'='1`
  - Result: Breaks out of the account filter and retrieves transactions from all accounts.
- **Controlled Order By Injection**:
  `GET /api/transactions/filter?sortBy=(CASE WHEN (1=1) THEN created_at ELSE amount END)`
  - Result: Dynamically alters query ordering based on injected boolean expressions.

### Secure Remediation Target
1. Use parameter placeholders (`?`) for all dynamic filter values.
2. Enforce a strict static allowlist for acceptable sorting columns:
```java
// Method: filterSecure(...)
private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
    "created_at", "amount", "transaction_type", "account_id"
);

String validatedSort = (sortBy != null && ALLOWED_SORT_COLUMNS.contains(sortBy.toLowerCase()))
        ? sortBy.toLowerCase()
        : "created_at";

String validatedDirection = ("ASC".equalsIgnoreCase(sortDirection)) ? "ASC" : "DESC";

sql.append(" ORDER BY ").append(validatedSort).append(" ").append(validatedDirection);
```

---

## 4. Security Verification Notes
- **Functional Tests Pass**: Ordinary functional tests using valid synthetic inputs pass successfully on both vulnerable and secure implementations.
- **Functional Testing Limitation**: Standard unit and regression tests do not assert SQL injection safety. The Proactive Shift-Left Defense platform identifies the vulnerable patterns via static taint analysis and semantic code graph inspection.

