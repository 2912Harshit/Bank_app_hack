-- SecureBank Transactions Database Schema
-- Database: transactions_db

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(40) PRIMARY KEY,
    account_id VARCHAR(40) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255) NOT NULL,
    reference VARCHAR(80) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_positive_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_type ON transactions(transaction_type);

