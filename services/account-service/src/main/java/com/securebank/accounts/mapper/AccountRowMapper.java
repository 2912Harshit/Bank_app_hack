package com.securebank.accounts.mapper;

import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Component
public class AccountRowMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getString("account_id"));
        account.setUserId(rs.getString("user_id"));
        account.setCustomerName(rs.getString("customer_name"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
        account.setCurrency(rs.getString("currency"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(AccountStatus.valueOf(rs.getString("status")));

        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            account.setCreatedAt(createdTs.toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }

        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            account.setUpdatedAt(updatedTs.toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }

        return account;
    }
}
