package com.securebank.transactions.repository;

import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.mapper.TransactionRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Logger log = LoggerFactory.getLogger(TransactionRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final TransactionRowMapper rowMapper;

    // Strict allowlist used by the secure remediation implementation
    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "created_at", "amount", "transaction_type", "account_id"
    );

    public TransactionRepositoryImpl(JdbcTemplate jdbcTemplate, TransactionRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
            INSERT INTO transactions (transaction_id, account_id, transaction_type, amount,
                                      currency, description, reference, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getTransactionType().name(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getReference(),
                Timestamp.from(transaction.getCreatedAt().toInstant())
        );

        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try {
            Transaction txn = jdbcTemplate.queryForObject(sql, rowMapper, transactionId);
            return Optional.ofNullable(txn);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, accountId);
    }

    @Override
    public List<Transaction> search(String query) {
        if (query == null || query.isBlank()) {
            return filterSecure(null, null, null, null, null, null, "created_at", "DESC");
        }
        String sql = """
            SELECT * FROM transactions
            WHERE LOWER(description) LIKE LOWER(?) OR LOWER(reference) LIKE LOWER(?)
            ORDER BY created_at DESC
        """;
        String pattern = "%" + query.trim() + "%";
        return jdbcTemplate.query(sql, rowMapper, pattern, pattern);
    }

    /**
     * =========================================================================
     * INTENTIONALLY VULNERABLE DEMONSTRATION METHOD (Hackathon Scenario B)
     * Target: GET /api/transactions/filter
     *
     * Vulnerability Explanation:
     * 1. Value SQL Injection: String parameters (such as 'accountId') are directly
     *    concatenated into the WHERE clause without parameter placeholders:
     *        sql.append(" AND account_id = '").append(accountId).append("'");
     * 2. Identifier SQL Injection (ORDER BY): SQL identifiers (column names and directions)
     *    cannot be parameterized by JDBC PreparedStatement. Directly concatenating
     *    untrusted sortBy/sortDirection inputs into the ORDER BY clause exposes the query
     *    to syntax injection and second-order boolean-based blind techniques:
     *        sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDirection);
     * =========================================================================
     */
    @Override
    public List<Transaction> filter(String accountId,
                                    TransactionType transactionType,
                                    BigDecimal minAmount,
                                    BigDecimal maxAmount,
                                    OffsetDateTime startDate,
                                    OffsetDateTime endDate,
                                    String sortBy,
                                    String sortDirection) {
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        // Vulnerability 1: Direct string concatenation for accountId
        if (accountId != null && !accountId.isBlank()) {
            sql.append(" AND account_id = '").append(accountId).append("'");
        }

        if (transactionType != null) {
            sql.append(" AND transaction_type = '").append(transactionType.name()).append("'");
        }

        if (minAmount != null) {
            sql.append(" AND amount >= ").append(minAmount);
        }

        if (maxAmount != null) {
            sql.append(" AND amount <= ").append(maxAmount);
        }

        if (startDate != null) {
            sql.append(" AND created_at >= '").append(Timestamp.from(startDate.toInstant())).append("'");
        }

        if (endDate != null) {
            sql.append(" AND created_at <= '").append(Timestamp.from(endDate.toInstant())).append("'");
        }

        // Vulnerability 2: Unsafe ORDER BY clause without allowlisting
        String orderField = (sortBy != null && !sortBy.isBlank()) ? sortBy : "created_at";
        String orderDir = (sortDirection != null && !sortDirection.isBlank()) ? sortDirection : "DESC";

        sql.append(" ORDER BY ").append(orderField).append(" ").append(orderDir);

        log.warn("[SECURITY DEMO - Scenario B] Executing vulnerable dynamic SQL: {}", sql);

        return jdbcTemplate.query(sql.toString(), rowMapper);
    }

    /**
     * =========================================================================
     * SECURE REMEDIATION TARGET (Hackathon Scenario B Target)
     *
     * Remediation Explanation:
     * 1. Uses PreparedStatement parameter binding ('?') for all filter values.
     * 2. Validates sort columns against a strict static allowlist (ALLOWED_SORT_COLUMNS).
     * 3. Validates sort direction strictly to 'ASC' or 'DESC'.
     * =========================================================================
     */
    @Override
    public List<Transaction> filterSecure(String accountId,
                                          TransactionType transactionType,
                                          BigDecimal minAmount,
                                          BigDecimal maxAmount,
                                          OffsetDateTime startDate,
                                          OffsetDateTime endDate,
                                          String sortBy,
                                          String sortDirection) {
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (accountId != null && !accountId.isBlank()) {
            sql.append(" AND account_id = ?");
            params.add(accountId.trim());
        }

        if (transactionType != null) {
            sql.append(" AND transaction_type = ?");
            params.add(transactionType.name());
        }

        if (minAmount != null) {
            sql.append(" AND amount >= ?");
            params.add(minAmount);
        }

        if (maxAmount != null) {
            sql.append(" AND amount <= ?");
            params.add(maxAmount);
        }

        if (startDate != null) {
            sql.append(" AND created_at >= ?");
            params.add(Timestamp.from(startDate.toInstant()));
        }

        if (endDate != null) {
            sql.append(" AND created_at <= ?");
            params.add(Timestamp.from(endDate.toInstant()));
        }

        String validatedSort = (sortBy != null && ALLOWED_SORT_COLUMNS.contains(sortBy.toLowerCase()))
                ? sortBy.toLowerCase()
                : "created_at";

        String validatedDirection = ("ASC".equalsIgnoreCase(sortDirection)) ? "ASC" : "DESC";

        sql.append(" ORDER BY ").append(validatedSort).append(" ").append(validatedDirection);

        return jdbcTemplate.query(sql.toString(), rowMapper, params.toArray());
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM transactions";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
