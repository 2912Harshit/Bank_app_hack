package com.securebank.accounts.dto.request;

import com.securebank.accounts.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateAccountStatusRequest {

    @NotNull(message = "status is required")
    private AccountStatus status;

    public UpdateAccountStatusRequest() {
    }

    public UpdateAccountStatusRequest(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
