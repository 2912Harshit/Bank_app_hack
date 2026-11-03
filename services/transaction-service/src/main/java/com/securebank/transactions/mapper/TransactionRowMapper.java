package com.securebank.transactions.mapper;

import com.securebank.transactions.entity.Transaction;
import com.securebank.transactions.entity.TransactionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Component
public class TransactionRowMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getString("transaction_id"));
        transaction.setAccountId(rs.getString("account_id"));
        transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setCurrency(rs.getString("currency"));
        transaction.setDescription(rs.getString("description"));
        transaction.setReference(rs.getString("reference"));

        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            transaction.setCreatedAt(createdTs.toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }

        return transaction;
    }
}

