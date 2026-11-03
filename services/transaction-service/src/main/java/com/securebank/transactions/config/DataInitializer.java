package com.securebank.transactions.config;

import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.repository.TransactionRepository;
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

    private final TransactionRepository transactionRepository;

    public DataInitializer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (transactionRepository.count() == 0) {
            log.info("Seeding synthetic initial transactions into transactions_db...");

            OffsetDateTime baseTime = OffsetDateTime.of(2026, 11, 3, 10, 30, 0, 0, ZoneOffset.UTC);

            transactionRepository.save(new Transaction(
                    "txn-1001",
                    "acc-1001",
                    TransactionType.DEBIT,
                    new BigDecimal("1500.00"),
                    "INR",
                    "Electricity bill payment",
                    "REF-20261103-001",
                    baseTime
            ));

            transactionRepository.save(new Transaction(
                    "txn-1002",
                    "acc-1001",
                    TransactionType.CREDIT,
                    new BigDecimal("25000.00"),
                    "INR",
                    "Salary direct deposit",
                    "REF-20261101-002",
                    baseTime.minusDays(2)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1003",
                    "acc-1001",
                    TransactionType.DEBIT,
                    new BigDecimal("450.00"),
                    "INR",
                    "Coffee shop purchase",
                    "REF-20261102-003",
                    baseTime.minusDays(1)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1004",
                    "acc-1002",
                    TransactionType.CREDIT,
                    new BigDecimal("15000.00"),
                    "INR",
                    "Freelance consulting payout",
                    "REF-20261025-004",
                    baseTime.minusDays(9)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1005",
                    "acc-1002",
                    TransactionType.DEBIT,
                    new BigDecimal("2200.00"),
                    "INR",
                    "Grocery supermarket",
                    "REF-20261028-005",
                    baseTime.minusDays(6)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1006",
                    "acc-1003",
                    TransactionType.CREDIT,
                    new BigDecimal("50000.00"),
                    "INR",
                    "Business invoice INV-8821",
                    "REF-20261030-006",
                    baseTime.minusDays(4)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1007",
                    "acc-1003",
                    TransactionType.DEBIT,
                    new BigDecimal("12500.00"),
                    "INR",
                    "Office equipment rental",
                    "REF-20261031-007",
                    baseTime.minusDays(3)
            ));

            transactionRepository.save(new Transaction(
                    "txn-1008",
                    "acc-1004",
                    TransactionType.CREDIT,
                    new BigDecimal("8900.00"),
                    "INR",
                    "Account opening deposit",
                    "REF-20261101-008",
                    baseTime.minusDays(2)
            ));

            log.info("Successfully seeded 8 synthetic transactions.");
        } else {
            log.info("Transactions table already contains data, skipping initialization.");
        }
    }
}
