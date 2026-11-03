package com.securebank.accounts.service;

import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.dto.response.AccountResponse;
import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import com.securebank.accounts.exception.BadRequestException;
import com.securebank.accounts.exception.ResourceNotFoundException;
import com.securebank.accounts.mapper.AccountMapper;
import com.securebank.accounts.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        String accountId = "acc-" + UUID.randomUUID().toString().substring(0, 8);
        String accountNumber = generateAccountNumber();
        OffsetDateTime now = OffsetDateTime.now();

        // Synthetic initial balance assigned by bank policy (never accepted from frontend input)
        BigDecimal initialBalance = request.getAccountType() == AccountType.CURRENT
                ? new BigDecimal("50000.00")
                : new BigDecimal("25000.00");

        Account account = new Account(
                accountId,
                request.getUserId().trim(),
                request.getCustomerName().trim(),
                accountNumber,
                request.getAccountType(),
                request.getCurrency().trim().toUpperCase(),
                initialBalance,
                AccountStatus.ACTIVE,
                now,
                now
        );

        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponse getAccountById(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> listAccounts(String userId, String status) {
        return accountRepository.findAll(userId, status)
                .stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponse> searchAccountsByName(String name) {
        return accountRepository.searchByCustomerName(name)
                .stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse updateAccountStatus(String accountId, UpdateAccountStatusRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Cannot update status of a CLOSED account");
        }

        OffsetDateTime now = OffsetDateTime.now();
        accountRepository.updateStatus(accountId, request.getStatus(), now);

        account.setStatus(request.getStatus());
        account.setUpdatedAt(now);
        return accountMapper.toResponse(account);
    }

    private String generateAccountNumber() {
        long randomDigits = ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L);
        return "SB" + randomDigits;
    }
}
