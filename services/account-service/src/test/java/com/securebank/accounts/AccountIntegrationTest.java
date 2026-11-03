package com.securebank.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.entity.AccountStatus;
import com.securebank.accounts.entity.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFullAccountLifecycle() throws Exception {
        // 1. Create a new account
        CreateAccountRequest createReq = new CreateAccountRequest(
                "user-integration-test",
                "Integration Test User",
                AccountType.SAVINGS,
                "INR"
        );

        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.accountNumber").exists())
                .andExpect(jsonPath("$.customerName").value("Integration Test User"))
                .andExpect(jsonPath("$.balance").value(25000.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        String createdAccountId = objectMapper.readTree(responseJson).get("accountId").asText();
        assertNotNull(createdAccountId);

        // 2. Fetch the created account by ID
        mockMvc.perform(get("/api/accounts/" + createdAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(createdAccountId))
                .andExpect(jsonPath("$.customerName").value("Integration Test User"));

        // 3. Search account by full customer name (valid input passes functional test)
        mockMvc.perform(get("/api/accounts/search?name=Integration Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Integration Test User"));

        // 4. Update account status to FROZEN
        UpdateAccountStatusRequest updateReq = new UpdateAccountStatusRequest(AccountStatus.FROZEN);
        mockMvc.perform(patch("/api/accounts/" + createdAccountId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FROZEN"));

        // 5. Query non-existent account returns 404
        mockMvc.perform(get("/api/accounts/acc-non-existent-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 6. Validation error returns 400
        CreateAccountRequest invalidReq = new CreateAccountRequest(
                "",
                "",
                null,
                "TOOLONG"
        );
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Demonstrates the controlled SQL Injection vulnerability (Scenario A).
     * By injecting "' OR '1'='1", the query string becomes:
     * SELECT * FROM accounts WHERE customer_name = '' OR '1'='1'
     * which returns all accounts in the table.
     */
    @Test
    void testScenarioASqlInjectionDemonstration() throws Exception {
        mockMvc.perform(get("/api/accounts/search?name=' OR '1'='1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(1)));
    }
}

