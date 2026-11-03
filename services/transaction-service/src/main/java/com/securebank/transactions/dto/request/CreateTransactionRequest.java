package com.securebank.transactions.dto.request;

import com.securebank.transactions.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateTransactionRequest {

    @NotBlank(message = "accountId is required")
    @Size(max = 40, message = "accountId must not exceed 40 characters")
    private String accountId;

    @NotNull(message = "transactionType is required (DEBIT or CREDIT)")
    private TransactionType transactionType;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    private String currency;

    @NotBlank(message = "description is required")
    @Size(max = 255, message = "description must not exceed 255 characters")
    private String description;

    @Size(max = 80, message = "reference must not exceed 80 characters")
    private String reference;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(String accountId, TransactionType transactionType, BigDecimal amount,
                                    String description, String reference) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.reference = reference;
        this.currency = "INR";
    }

    public CreateTransactionRequest(String accountId, TransactionType transactionType, BigDecimal amount,
                                    String currency, String description, String reference) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.reference = reference;
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
}

