package com.securebank.transactions.service;

import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.exception.ResourceNotFoundException;
import com.securebank.transactions.mapper.TransactionMapper;
import com.securebank.transactions.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionMapper transactionMapper;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
        transactionService = new TransactionServiceImpl(transactionRepository, transactionMapper);
    }

    @Test
    void createTransaction_success() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "Electricity bill payment",
                "REF-20261103-001"
        );

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertTrue(response.getTransactionId().startsWith("txn-"));
        assertEquals("acc-1001", response.getAccountId());
        assertEquals(TransactionType.DEBIT, response.getTransactionType());
        assertEquals(new BigDecimal("1500.00"), response.getAmount());
        assertEquals("Electricity bill payment", response.getDescription());
        assertEquals("REF-20261103-001", response.getReference());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactionById_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Transaction txn = new Transaction(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                now
        );

        when(transactionRepository.findById("txn-1001")).thenReturn(Optional.of(txn));

        TransactionResponse response = transactionService.getTransactionById("txn-1001");

        assertNotNull(response);
        assertEquals("txn-1001", response.getTransactionId());
        assertEquals(new BigDecimal("1500.00"), response.getAmount());
    }

    @Test
    void getTransactionById_notFound_throwsResourceNotFoundException() {
        when(transactionRepository.findById("txn-9999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById("txn-9999"));
    }

    @Test
    void getTransactionsByAccountId_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Transaction txn = new Transaction(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                now
        );

        when(transactionRepository.findByAccountId("acc-1001")).thenReturn(List.of(txn));

        List<TransactionResponse> results = transactionService.getTransactionsByAccountId("acc-1001");

        assertEquals(1, results.size());
        assertEquals("acc-1001", results.get(0).getAccountId());
    }

    @Test
    void filterTransactions_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Transaction txn = new Transaction(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                now
        );

        when(transactionRepository.filter("acc-1001", TransactionType.DEBIT, null, null, null, null, "created_at", "DESC"))
                .thenReturn(List.of(txn));

        List<TransactionResponse> results = transactionService.filterTransactions(
                "acc-1001", TransactionType.DEBIT, null, null, null, null, "created_at", "DESC"
        );

        assertEquals(1, results.size());
        assertEquals(TransactionType.DEBIT, results.get(0).getTransactionType());
    }
}

