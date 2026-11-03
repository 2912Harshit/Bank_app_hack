package com.securebank.transactions.mapper;

import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction txn) {
        if (txn == null) {
            return null;
        }
        return new TransactionResponse(
                txn.getTransactionId(),
                txn.getAccountId(),
                txn.getTransactionType(),
                txn.getAmount(),
                txn.getCurrency(),
                txn.getDescription(),
                txn.getReference(),
                txn.getCreatedAt()
        );
    }
}

