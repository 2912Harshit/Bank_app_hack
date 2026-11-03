package com.securebank.transactions.service;

import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.exception.ResourceNotFoundException;
import com.securebank.transactions.mapper.TransactionMapper;
import com.securebank.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        String transactionId = "txn-" + UUID.randomUUID().toString().substring(0, 8);

        String reference = (request.getReference() != null && !request.getReference().isBlank())
                ? request.getReference().trim()
                : "REF-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(100, 999);

        String currency = (request.getCurrency() != null && !request.getCurrency().isBlank())
                ? request.getCurrency().trim().toUpperCase()
                : "INR";

        OffsetDateTime now = OffsetDateTime.now();

        Transaction txn = new Transaction(
                transactionId,
                request.getAccountId().trim(),
                request.getTransactionType(),
                request.getAmount(),
                currency,
                request.getDescription().trim(),
                reference,
                now
        );

        Transaction saved = transactionRepository.save(txn);
        return transactionMapper.toResponse(saved);
    }

    @Override
    public TransactionResponse getTransactionById(String transactionId) {
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return transactionMapper.toResponse(txn);
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> searchTransactions(String query) {
        return transactionRepository.search(query)
                .stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> filterTransactions(String accountId,
                                                        TransactionType transactionType,
                                                        BigDecimal minAmount,
                                                        BigDecimal maxAmount,
                                                        OffsetDateTime startDate,
                                                        OffsetDateTime endDate,
                                                        String sortBy,
                                                        String sortDirection) {
        return transactionRepository.filter(
                accountId, transactionType, minAmount, maxAmount, startDate, endDate, sortBy, sortDirection
        ).stream().map(transactionMapper::toResponse).collect(Collectors.toList());
    }
}
