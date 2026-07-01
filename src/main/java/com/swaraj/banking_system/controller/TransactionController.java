package com.swaraj.banking_system.controller;

import com.swaraj.banking_system.dto.request.DepositRequest;
import com.swaraj.banking_system.dto.request.TransferRequest;
import com.swaraj.banking_system.dto.request.WithdrawRequest;
import com.swaraj.banking_system.dto.response.TransactionHistoryResponse;
import com.swaraj.banking_system.dto.response.TransactionResponse;


import com.swaraj.banking_system.service.interfaces.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request
    ) {

        TransactionResponse response =
                transactionService.deposit(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid
            @RequestBody
            WithdrawRequest request
    ) {

        TransactionResponse response =
                transactionService.withdraw(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid
            @RequestBody
            TransferRequest request
    ) {

        TransactionResponse response =
                transactionService.transfer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionHistoryResponse>>
    getTransactionHistory(

            @PathVariable Long accountId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Page<TransactionHistoryResponse> response =
                transactionService.getTransactionHistory(
                        accountId,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

}