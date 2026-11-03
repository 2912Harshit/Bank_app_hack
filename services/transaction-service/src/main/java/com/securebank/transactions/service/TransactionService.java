package com.securebank.transactions.service;

import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest request);

    TransactionResponse getTransactionById(String transactionId);

    List<TransactionResponse> getTransactionsByAccountId(String accountId);

    List<TransactionResponse> searchTransactions(String query);

    List<TransactionResponse> filterTransactions(String accountId,
                                                TransactionType transactionType,
                                                BigDecimal minAmount,
                                                BigDecimal maxAmount,
                                                OffsetDateTime startDate,
                                                OffsetDateTime endDate,
                                                String sortBy,
                                                String sortDirection);
}

