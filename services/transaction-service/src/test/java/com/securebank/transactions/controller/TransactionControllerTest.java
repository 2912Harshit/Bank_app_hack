package com.securebank.transactions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.exception.ResourceNotFoundException;
import com.securebank.transactions.service.TransactionService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createTransaction_validRequest_returns201() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "Electricity bill payment",
                "REF-20261103-001"
        );

        TransactionResponse response = new TransactionResponse(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                OffsetDateTime.now()
        );

        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("txn-1001"))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.transactionType").value("DEBIT"));
    }

    @Test
    void createTransaction_invalidRequest_returns400() throws Exception {
        CreateTransactionRequest invalid = new CreateTransactionRequest(
                "", // Blank accountId
                null, // Null transactionType
                new BigDecimal("-50.00"), // Negative amount
                "", // Blank description
                null
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.accountId").exists());
    }

    @Test
    void getTransactionById_found_returns200() throws Exception {
        TransactionResponse response = new TransactionResponse(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                OffsetDateTime.now()
        );

        when(transactionService.getTransactionById("txn-1001")).thenReturn(response);

        mockMvc.perform(get("/api/transactions/txn-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("txn-1001"))
                .andExpect(jsonPath("$.reference").value("REF-20261103-001"));
    }

    @Test
    void getTransactionById_notFound_returns404() throws Exception {
        when(transactionService.getTransactionById("txn-9999"))
                .thenThrow(new ResourceNotFoundException("Transaction not found with id: txn-9999"));

        mockMvc.perform(get("/api/transactions/txn-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getTransactionsByAccountId_returns200() throws Exception {
        TransactionResponse response = new TransactionResponse(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                OffsetDateTime.now()
        );

        when(transactionService.getTransactionsByAccountId("acc-1001")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/transactions/account/acc-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("acc-1001"));
    }

    @Test
    void filterTransactions_returns200() throws Exception {
        TransactionResponse response = new TransactionResponse(
                "txn-1001",
                "acc-1001",
                TransactionType.DEBIT,
                new BigDecimal("1500.00"),
                "INR",
                "Electricity bill payment",
                "REF-20261103-001",
                OffsetDateTime.now()
        );

        when(transactionService.filterTransactions(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/transactions/filter?accountId=acc-1001&transactionType=DEBIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("txn-1001"));
    }
}
