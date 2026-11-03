package com.securebank.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.dto.response.AccountResponse;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import com.securebank.accounts.exception.ResourceNotFoundException;
import com.securebank.accounts.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_validRequest_returns201() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest(
                "user-001",
                "Aarav Sharma",
                AccountType.SAVINGS,
                "INR"
        );

        AccountResponse response = new AccountResponse(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                OffsetDateTime.now()
        );

        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value("acc-1001"))
                .andExpect(jsonPath("$.customerName").value("Aarav Sharma"))
                .andExpect(jsonPath("$.balance").value(25000.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createAccount_invalidRequest_returns400() throws Exception {
        CreateAccountRequest invalidRequest = new CreateAccountRequest(
                "", // Blank userId
                "",
                null,
                "INVALID_CURRENCY"
        );

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.userId").exists());
    }

    @Test
    void getAccountById_found_returns200() throws Exception {
        AccountResponse response = new AccountResponse(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                OffsetDateTime.now()
        );

        when(accountService.getAccountById("acc-1001")).thenReturn(response);

        mockMvc.perform(get("/api/accounts/acc-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("acc-1001"))
                .andExpect(jsonPath("$.accountNumber").value("SB1000001001"));
    }

    @Test
    void getAccountById_notFound_returns404() throws Exception {
        when(accountService.getAccountById("acc-9999"))
                .thenThrow(new ResourceNotFoundException("Account not found with id: acc-9999"));

        mockMvc.perform(get("/api/accounts/acc-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account not found with id: acc-9999"));
    }

    @Test
    void searchAccounts_returns200() throws Exception {
        AccountResponse response = new AccountResponse(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.ACTIVE,
                OffsetDateTime.now()
        );

        when(accountService.searchAccountsByName("Aarav")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/accounts/search?name=Aarav"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("acc-1001"))
                .andExpect(jsonPath("$[0].customerName").value("Aarav Sharma"));
    }

    @Test
    void updateAccountStatus_valid_returns200() throws Exception {
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(AccountStatus.FROZEN);
        AccountResponse response = new AccountResponse(
                "acc-1001",
                "user-001",
                "Aarav Sharma",
                "SB1000001001",
                AccountType.SAVINGS,
                "INR",
                new BigDecimal("25000.00"),
                AccountStatus.FROZEN,
                OffsetDateTime.now()
        );

        when(accountService.updateAccountStatus(eq("acc-1001"), any(UpdateAccountStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/accounts/acc-1001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"));
    }
}

