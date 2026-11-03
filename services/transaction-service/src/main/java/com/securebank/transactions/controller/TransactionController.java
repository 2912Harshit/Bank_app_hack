package com.securebank.transactions.controller;

import com.securebank.transactions.dto.request.CreateTransactionRequest;
import com.securebank.transactions.dto.response.TransactionResponse;
import com.securebank.transactions.entity.TransactionType;
import com.securebank.transactions.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable String transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable String accountId) {
        List<TransactionResponse> history = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
            @RequestParam(required = false) String query) {
        List<TransactionResponse> results = transactionService.searchTransactions(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> filterTransactions(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(required = false, defaultValue = "created_at") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        List<TransactionResponse> results = transactionService.filterTransactions(
                accountId, transactionType, minAmount, maxAmount, startDate, endDate, sortBy, sortDirection
        );
        return ResponseEntity.ok(results);
    }
}
