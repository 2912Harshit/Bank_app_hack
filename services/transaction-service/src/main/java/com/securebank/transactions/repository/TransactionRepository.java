package com.securebank.transactions.repository;

import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String transactionId);

    List<Transaction> findByAccountId(String accountId);

    List<Transaction> search(String query);

    /**
     * Filters transactions using dynamic query construction.
     * Uses the demonstration implementation which contains intentional SQL Injection flaws.
     */
    List<Transaction> filter(String accountId,
                             TransactionType transactionType,
                             BigDecimal minAmount,
                             BigDecimal maxAmount,
                             OffsetDateTime startDate,
                             OffsetDateTime endDate,
                             String sortBy,
                             String sortDirection);

    /**
     * Secure remediation target for transaction filtering using parameterized values and column allowlisting.
     */
    List<Transaction> filterSecure(String accountId,
                                   TransactionType transactionType,
                                   BigDecimal minAmount,
                                   BigDecimal maxAmount,
                                   OffsetDateTime startDate,
                                   OffsetDateTime endDate,
                                   String sortBy,
                                   String sortDirection);

    long count();
}
