-- SecureBank Accounts Database Schema
-- Database: accounts_db

CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(40) PRIMARY KEY,
    user_id VARCHAR(40) NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    account_number VARCHAR(30) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_account_type CHECK (account_type IN ('SAVINGS', 'CURRENT')),
    CONSTRAINT chk_account_status CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    CONSTRAINT chk_non_negative_balance CHECK (balance >= 0)
);

CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_customer_name ON accounts(customer_name);
