package com.securebank.accounts.dto.response;

import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AccountResponse {
    private String accountId;
    private String userId;
    private String customerName;
    private String accountNumber;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private AccountStatus status;
    private OffsetDateTime createdAt;

    public AccountResponse() {
    }

    public AccountResponse(String accountId, String userId, String customerName, String accountNumber,
                           AccountType accountType, String currency, BigDecimal balance,
                           AccountStatus status, OffsetDateTime createdAt) {
        this.accountId = accountId;
        this.userId = userId;
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
