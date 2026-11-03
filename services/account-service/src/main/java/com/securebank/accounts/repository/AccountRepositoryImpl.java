package com.securebank.accounts.repository;

import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.mapper.AccountRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private static final Logger log = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final AccountRowMapper rowMapper;

    public AccountRepositoryImpl(JdbcTemplate jdbcTemplate, AccountRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Account save(Account account) {
        String sql = """
            INSERT INTO accounts (account_id, user_id, customer_name, account_number, account_type,
                                  currency, balance, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                account.getAccountId(),
                account.getUserId(),
                account.getCustomerName(),
                account.getAccountNumber(),
                account.getAccountType().name(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus().name(),
                Timestamp.from(account.getCreatedAt().toInstant()),
                Timestamp.from(account.getUpdatedAt().toInstant())
        );

        return account;
    }

    @Override
    public Optional<Account> findById(String accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, rowMapper, accountId);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, rowMapper, accountNumber);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> findAll(String userId, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM accounts WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null && !userId.isBlank()) {
            sql.append(" AND user_id = ?");
            params.add(userId.trim());
        }

        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            params.add(status.trim().toUpperCase());
        }

        sql.append(" ORDER BY created_at DESC");

        return jdbcTemplate.query(sql.toString(), rowMapper, params.toArray());
    }

    /**
     * =========================================================================
     * INTENTIONALLY VULNERABLE DEMONSTRATION METHOD (Hackathon Scenario A)
     * Target: GET /api/accounts/search?name=Aarav
     *
     * Vulnerability Explanation:
     * The input parameter 'name' is concatenated directly into the dynamic SQL query
     * string without parameterization:
     *     String sql = "SELECT * FROM accounts WHERE customer_name = '" + name + "'";
     *
     * This flaw allows an untrusted caller to break out of the string literal by supplying
     * SQL metacharacters (e.g. `' OR '1'='1`) to alter query execution logic.
     * =========================================================================
     */
    @Override
    public List<Account> searchByCustomerName(String name) {
        if (name == null || name.isBlank()) {
            return findAll(null, null);
        }

        // Intentionally vulnerable string concatenation:
        String sql = "SELECT * FROM accounts WHERE customer_name = '" + name + "'";
        log.warn("[SECURITY DEMO - Scenario A] Executing vulnerable SQL: {}", sql);

        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * =========================================================================
     * SECURE REMEDIATION TARGET (Hackathon Scenario A Target)
     *
     * Remediation Explanation:
     * Uses PreparedStatement parameter binding ('?') with JdbcTemplate.
     * The database driver treats the value strictly as data rather than executable
     * SQL syntax, completely eliminating SQL Injection vulnerabilities.
     * =========================================================================
     */
    @Override
    public List<Account> searchByCustomerNameSecure(String name) {
        if (name == null || name.isBlank()) {
            return findAll(null, null);
        }

        String sql = "SELECT * FROM accounts WHERE LOWER(customer_name) LIKE LOWER(?) ORDER BY customer_name ASC";
        return jdbcTemplate.query(sql, rowMapper, "%" + name.trim() + "%");
    }

    @Override
    public int updateStatus(String accountId, AccountStatus status, OffsetDateTime updatedAt) {
        String sql = "UPDATE accounts SET status = ?, updated_at = ? WHERE account_id = ?";
        return jdbcTemplate.update(sql,
                status.name(),
                Timestamp.from(updatedAt.toInstant()),
                accountId
        );
    }

    @Override
    public boolean existsById(String accountId) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, accountId);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM accounts";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
