package com.securebank.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.entity.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullTransactionLifecycle() throws Exception {
        // 1. Create a transaction
        CreateTransactionRequest createReq = new CreateTransactionRequest(
                "acc-integration-test",
                TransactionType.CREDIT,
                new BigDecimal("5000.00"),
                "INR",
                "Integration test bonus credit",
                "REF-INT-" + System.currentTimeMillis()
        );

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.transactionType").value("CREDIT"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String createdTxnId = objectMapper.readTree(json).get("transactionId").asText();
        assertNotNull(createdTxnId);

        // 2. Fetch transaction by ID
        mockMvc.perform(get("/api/transactions/" + createdTxnId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(createdTxnId))
                .andExpect(jsonPath("$.description").value("Integration test bonus credit"));

        // 3. Get history by account ID
        mockMvc.perform(get("/api/transactions/account/acc-integration-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("acc-integration-test"));

        // 4. Search transactions
        mockMvc.perform(get("/api/transactions/search?query=bonus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Integration test bonus credit"));

        // 5. Filter transactions
        mockMvc.perform(get("/api/transactions/filter?accountId=acc-integration-test&transactionType=CREDIT&minAmount=1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("acc-integration-test"));

        // 6. Non-existent transaction returns 404
        mockMvc.perform(get("/api/transactions/txn-non-existent-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 7. Invalid transaction returns 400
        CreateTransactionRequest invalid = new CreateTransactionRequest(
                "",
                null,
                new BigDecimal("-10.00"),
                "",
                null
        );
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Demonstrates the controlled SQL Injection vulnerability (Scenario B).
     * 1. Value Injection: By injecting "' OR '1'='1" into accountId, the query breaks out
     *    and returns transactions across all accounts.
     * 2. Order By Injection: By injecting a dynamic expression into sortBy, the ORDER BY
     *    clause executes the injected logic.
     */
    @Test
    void testScenarioBSqlInjectionDemonstration() throws Exception {
        // Value Injection demonstration
        mockMvc.perform(get("/api/transactions/filter?accountId=acc-1001' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(1)));

        // Order By Injection demonstration
        mockMvc.perform(get("/api/transactions/filter?sortBy=amount&sortDirection=ASC"))
                .andExpect(status().isOk());
    }
}

