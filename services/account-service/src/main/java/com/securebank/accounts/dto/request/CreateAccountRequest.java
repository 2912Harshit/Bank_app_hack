package com.securebank.accounts.dto.request;

import com.securebank.accounts.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateAccountRequest {

    @NotBlank(message = "userId is required")
    @Size(max = 40, message = "userId must not exceed 40 characters")
    private String userId;

    @NotBlank(message = "customerName is required")
    @Size(max = 120, message = "customerName must not exceed 120 characters")
    private String customerName;

    @NotNull(message = "accountType is required")
    private AccountType accountType;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code (e.g., INR, USD)")
    private String currency;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(String userId, String customerName, AccountType accountType, String currency) {
        this.userId = userId;
        this.customerName = customerName;
        this.accountType = accountType;
        this.currency = currency;
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
}
