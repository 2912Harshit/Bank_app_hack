package com.securebank.accounts.service;

import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.dto.response.AccountResponse;
import com.securebank.accounts.entity.Account;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import com.securebank.accounts.exception.BadRequestException;
import com.securebank.accounts.exception.ResourceNotFoundException;
import com.securebank.accounts.mapper.AccountMapper;
import com.securebank.accounts.repository.AccountRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountMapper accountMapper;
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
        accountService = new AccountServiceImpl(accountRepository, accountMapper);
    }

    @Test
    void createAccount_success() {
        CreateAccountRequest request = new CreateAccountRequest(
                "user-001",
                "Aarav Sharma",
                AccountType.SAVINGS,
                "INR"
        );

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertNotNull(response.getAccountId());
        assertTrue(response.getAccountId().startsWith("acc-"));
        assertNotNull(response.getAccountNumber());
        assertTrue(response.getAccountNumber().startsWith("SB"));
        assertEquals("user-001", response.getUserId());
        assertEquals("Aarav Sharma", response.getCustomerName());
        assertEquals(AccountType.SAVINGS, response.getAccountType());
        assertEquals("INR", response.getCurrency());
        assertEquals(new BigDecimal("25000.00"), response.getBalance());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                now,
                now
        );

        when(accountRepository.findById("acc-1001")).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById("acc-1001");

        assertNotNull(response);
        assertEquals("acc-1001", response.getAccountId());
        assertEquals("Aarav Sharma", response.getCustomerName());
    }

    @Test
    void getAccountById_notFound_throwsResourceNotFoundException() {
        when(accountRepository.findById("acc-9999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById("acc-9999"));
    }

    @Test
    void searchAccountsByName_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                now,
                now
        );

        when(accountRepository.searchByCustomerName("Aarav")).thenReturn(List.of(account));

        List<AccountResponse> results = accountService.searchAccountsByName("Aarav");

        assertEquals(1, results.size());
        assertEquals("Aarav Sharma", results.get(0).getCustomerName());
    }

    @Test
    void updateAccountStatus_success() {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                now,
                now
        );

        when(accountRepository.findById("acc-1001")).thenReturn(Optional.of(account));
        when(accountRepository.updateStatus(eq("acc-1001"), eq(AccountStatus.FROZEN), any(OffsetDateTime.class))).thenReturn(1);

        AccountResponse response = accountService.updateAccountStatus("acc-1001", new UpdateAccountStatusRequest(AccountStatus.FROZEN));

        assertEquals(AccountStatus.FROZEN, response.getStatus());
        verify(accountRepository, times(1)).updateStatus(eq("acc-1001"), eq(AccountStatus.FROZEN), any(OffsetDateTime.class));
    }

    @Test
    void updateAccountStatus_closedAccount_throwsBadRequestException() {
        OffsetDateTime now = OffsetDateTime.now();
        Account account = new Account(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.CLOSED,
                now,
                now
        );

        when(accountRepository.findById("acc-1001")).thenReturn(Optional.of(account));

        assertThrows(BadRequestException.class, () ->
                accountService.updateAccountStatus("acc-1001", new UpdateAccountStatusRequest(AccountStatus.ACTIVE)));
    }
}
