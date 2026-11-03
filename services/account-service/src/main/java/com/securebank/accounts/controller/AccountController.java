package com.securebank.accounts.controller;

import com.securebank.accounts.dto.request.CreateAccountRequest;
import com.securebank.accounts.dto.request.UpdateAccountStatusRequest;
import com.securebank.accounts.dto.response.AccountResponse;
import com.securebank.accounts.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String accountId) {
        AccountResponse response = accountService.getAccountById(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listAccounts(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {
        List<AccountResponse> accounts = accountService.listAccounts(userId, status);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountResponse>> searchAccounts(@RequestParam(required = false) String name) {
        List<AccountResponse> accounts = accountService.searchAccountsByName(name);
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{accountId}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable String accountId,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        AccountResponse response = accountService.updateAccountStatus(accountId, request);
        return ResponseEntity.ok(response);
    }
}
