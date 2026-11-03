package com.securebank.transactions.dto.response;

import com.securebank.transactions.entity.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionResponse {
    private String transactionId;
    private String accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String reference;
    private OffsetDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(String transactionId, String accountId, TransactionType transactionType,
                               BigDecimal amount, String currency, String description,
                               String reference, OffsetDateTime createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
