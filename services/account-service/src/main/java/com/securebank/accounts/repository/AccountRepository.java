package com.securebank.accounts.repository;

import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(String accountId);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAll(String userId, String status);

    List<Account> searchByCustomerName(String name);

    int updateStatus(String accountId, AccountStatus status, OffsetDateTime updatedAt);

    boolean existsById(String accountId);

    long count();
}

