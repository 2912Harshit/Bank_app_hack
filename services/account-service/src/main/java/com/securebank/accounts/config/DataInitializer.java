package com.securebank.accounts.config;

import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import com.securebank.accounts.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AccountRepository accountRepository;

    public DataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) {
        if (accountRepository.count() == 0) {
            log.info("Seeding synthetic initial accounts into accounts_db...");

            OffsetDateTime baseTime = OffsetDateTime.of(2026, 11, 3, 10, 0, 0, 0, ZoneOffset.UTC);

            accountRepository.save(new Account(
                    "acc-1001",
                    "user-001",
                    "Aarav Sharma",
                    "SB1000001001",
                    AccountType.SAVINGS,
                    "INR",
                    new BigDecimal("25000.00"),
                    AccountStatus.ACTIVE,
                    baseTime,
                    baseTime
            ));

            accountRepository.save(new Account(
                    "acc-1002",
                    "user-002",
                    "Priya Patel",
                    "SB1000001002",
                    AccountType.SAVINGS,
                    "INR",
                    new BigDecimal("18500.50"),
                    AccountStatus.ACTIVE,
                    baseTime.minusDays(10),
                    baseTime.minusDays(10)
            ));

            accountRepository.save(new Account(
                    "acc-1003",
                    "user-002",
                    "Priya Patel",
                    "SB1000001003",
                    AccountType.CURRENT,
                    "INR",
                    new BigDecimal("52000.00"),
                    AccountStatus.ACTIVE,
                    baseTime.minusDays(5),
                    baseTime.minusDays(5)
            ));

            accountRepository.save(new Account(
                    "acc-1004",
                    "user-003",
                    "Rohan Verma",
                    "SB1000001004",
                    AccountType.SAVINGS,
                    "INR",
                    new BigDecimal("8900.00"),
                    AccountStatus.ACTIVE,
                    baseTime.minusDays(2),
                    baseTime.minusDays(2)
            ));

            accountRepository.save(new Account(
                    "acc-1005",
                    "user-004",
                    "Neha Gupta",
                    "SB1000001005",
                    AccountType.SAVINGS,
                    "INR",
                    new BigDecimal("45000.00"),
                    AccountStatus.FROZEN,
                    baseTime.minusDays(15),
                    baseTime.minusDays(1)
            ));

            log.info("Successfully seeded 5 synthetic accounts.");
        } else {
            log.info("Accounts table already contains data, skipping initialization.");
        }
    }
}
