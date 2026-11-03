# SecureBank — API Documentation

This document provides complete API reference contracts for both SecureBank microservices.

---

## 1. Account Service API

**Base URL**: `http://localhost:8081/api`

### 1.1 Create Account
- **Method**: `POST`
- **Path**: `/accounts`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "userId": "user-001",
    "customerName": "Aarav Sharma",
    "accountType": "SAVINGS",
    "currency": "INR"
  }
  ```
- **Validation Rules**:
  - `userId`: Required, max 40 characters.
  - `customerName`: Required, max 120 characters.
  - `accountType`: Required, must be `SAVINGS` or `CURRENT`.
  - `currency`: Required, 3-character ISO code (e.g. `INR`).
- **Response**: `201 Created`
  ```json
  {
    "accountId": "acc-a1b2c3d4",
    "userId": "user-001",
    "customerName": "Aarav Sharma",
    "accountNumber": "SB1083921823",
    "accountType": "SAVINGS",
    "currency": "INR",
    "balance": 25000.00,
    "status": "ACTIVE",
    "createdAt": "2026-11-03T10:00:00Z"
  }
  ```

---

### 1.2 Get Account Details
- **Method**: `GET`
- **Path**: `/accounts/{accountId}`
- **Response**: `200 OK`
  ```json
  {
    "accountId": "acc-1001",
    "userId": "user-001",
    "customerName": "Aarav Sharma",
    "accountNumber": "SB1000001001",
    "accountType": "SAVINGS",
    "currency": "INR",
    "balance": 25000.00,
    "status": "ACTIVE",
    "createdAt": "2026-11-03T10:00:00Z"
  }
  ```
- **Error**: `404 Not Found` if the account does not exist.

---

### 1.3 List Accounts
- **Method**: `GET`
- **Path**: `/accounts`
- **Query Parameters**:
  - `userId` (optional): Filter by owner ID (e.g. `user-001`).
  - `status` (optional): Filter by status (`ACTIVE`, `FROZEN`, `CLOSED`).
- **Response**: `200 OK` (Array of Account objects)

---

### 1.4 Search Accounts by Customer Name
- **Method**: `GET`
- **Path**: `/accounts/search?name={name}`
- **Query Parameters**:
  - `name`: Customer name substring/equality search (e.g. `Aarav`).
- **Response**: `200 OK` (Array of Account objects)
- **Security Note**: This endpoint demonstrates Scenario A (unparameterized SQL query construction).

---

### 1.5 Update Account Status
- **Method**: `PATCH`
- **Path**: `/accounts/{accountId}/status`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "status": "FROZEN"
  }
  ```
- **Allowed Transitions**:
  - `ACTIVE` &harr; `FROZEN`
  - `ACTIVE` / `FROZEN` &rarr; `CLOSED`
  - Once `CLOSED`, accounts cannot transition to any other status.
- **Response**: `200 OK` (Updated Account object)
- **Error**: `400 Bad Request` if attempting to reopen a closed account.

---

## 2. Transaction Service API

**Base URL**: `http://localhost:8082/api`

### 2.1 Create Transaction Record
- **Method**: `POST`
- **Path**: `/transactions`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "accountId": "acc-1001",
    "transactionType": "DEBIT",
    "amount": 1500.00,
    "description": "Electricity bill payment",
    "reference": "REF-20261103-001"
  }
  ```
- **Validation Rules**:
  - `accountId`: Required, max 40 characters.
  - `transactionType`: Required (`DEBIT` or `CREDIT`).
  - `amount`: Required, must be > 0.00.
  - `description`: Required, max 255 characters.
  - `reference`: Optional, max 80 characters. If omitted, server generates a unique `REF-...` string.
- **Response**: `201 Created`
  ```json
  {
    "transactionId": "txn-f7e8d9c0",
    "accountId": "acc-1001",
    "transactionType": "DEBIT",
    "amount": 1500.00,
    "currency": "INR",
    "description": "Electricity bill payment",
    "reference": "REF-20261103-001",
    "createdAt": "2026-11-03T10:30:00Z"
  }
  ```

---

### 2.2 Get Transaction Details
- **Method**: `GET`
- **Path**: `/transactions/{transactionId}`
- **Response**: `200 OK`
  ```json
  {
    "transactionId": "txn-1001",
    "accountId": "acc-1001",
    "transactionType": "DEBIT",
    "amount": 1500.00,
    "currency": "INR",
    "description": "Electricity bill payment",
    "reference": "REF-20261103-001",
    "createdAt": "2026-11-03T10:30:00Z"
  }
  ```
- **Error**: `404 Not Found` if `transactionId` is invalid.

---

### 2.3 Get Account Transaction History
- **Method**: `GET`
- **Path**: `/transactions/account/{accountId}`
- **Response**: `200 OK` (Array of Transaction objects ordered by `created_at DESC`)

---

### 2.4 Search Transactions
- **Method**: `GET`
- **Path**: `/transactions/search?query={query}`
- **Query Parameters**:
  - `query`: Text query matched against `description` and `reference`.
- **Response**: `200 OK` (Array of Transaction objects)

---

### 2.5 Filter Transactions
- **Method**: `GET`
- **Path**: `/transactions/filter`
- **Query Parameters**:
  - `accountId` (optional): Filter by target account.
  - `transactionType` (optional): `DEBIT` or `CREDIT`.
  - `minAmount` (optional): Minimum amount threshold.
  - `maxAmount` (optional): Maximum amount threshold.
  - `startDate` (optional): ISO 8601 timestamp.
  - `endDate` (optional): ISO 8601 timestamp.
  - `sortBy` (optional, default: `created_at`): Sort field (`created_at`, `amount`, `transaction_type`, `account_id`).
  - `sortDirection` (optional, default: `DESC`): Sort direction (`ASC`, `DESC`).
- **Response**: `200 OK` (Array of Transaction objects)
- **Security Note**: This endpoint demonstrates Scenario B (dynamic WHERE value concatenation and ORDER BY clause injection).

---

## 3. Standard Error Response Contract

All errors from both services follow the RFC 7807 compatible structure:

```json
{
  "timestamp": "2026-11-03T15:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed for one or more fields",
  "path": "/api/accounts",
  "validationErrors": {
    "userId": "userId is required",
    "currency": "currency must be a 3-letter ISO code (e.g., INR, USD)"
  }
}
```
