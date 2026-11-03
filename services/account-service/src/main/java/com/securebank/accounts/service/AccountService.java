package com.securebank.accounts.service;

import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccountById(String accountId);

    List<AccountResponse> listAccounts(String userId, String status);

    List<AccountResponse> searchAccountsByName(String name);

    AccountResponse updateAccountStatus(String accountId, UpdateAccountStatusRequest request);
}
