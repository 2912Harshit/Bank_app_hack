package com.securebank.accounts.mapper;

import com.securebank.accounts.dto.response.AccountResponse;
import com.securebank.accounts.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountResponse(
                account.getAccountId(),
                account.getUserId(),
                account.getCustomerName(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}
