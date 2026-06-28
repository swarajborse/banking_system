package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.DepositRequest;
import com.swaraj.banking_system.dto.response.TransactionResponse;
import com.swaraj.banking_system.entity.BankAccount;
import com.swaraj.banking_system.entity.Transaction;
import com.swaraj.banking_system.entity.User;
import com.swaraj.banking_system.enums.AccountStatus;
import com.swaraj.banking_system.enums.TransactionStatus;
import com.swaraj.banking_system.enums.TransactionType;
import com.swaraj.banking_system.repository.BankAccountRepository;
import com.swaraj.banking_system.repository.TransactionRepository;
import com.swaraj.banking_system.repository.UserRepository;
import com.swaraj.banking_system.service.interfaces.TransactionService;
import com.swaraj.banking_system.util.TransactionReferenceGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            BankAccountRepository bankAccountRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TransactionResponse deposit(DepositRequest request) {

        // 1. Get logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 2. Fetch logged-in user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Fetch account
        BankAccount account = bankAccountRepository
                .findById(request.getAccountId())
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        // 4. Ownership validation
        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to access this account."
            );
        }

        // 5. Account status validation
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException(
                    "Account is not active."
            );
        }

        // 6. Update balance
        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

        // 7. Save updated account
        bankAccountRepository.save(account);

        // 8. Create transaction
        Transaction transaction = Transaction.builder()
                .referenceNumber(TransactionReferenceGenerator.generate())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .receiverAccount(account)
                .build();

        // 9. Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 10. Return response
        return TransactionResponse.builder()
                .referenceNumber(savedTransaction.getReferenceNumber())
                .transactionType(savedTransaction.getTransactionType())
                .transactionStatus(savedTransaction.getTransactionStatus())
                .amount(savedTransaction.getAmount())
                .description(savedTransaction.getDescription())
                .createdAt(savedTransaction.getCreatedAt())
                .senderAccountNumber(null)
                .receiverAccountNumber(account.getAccountNumber())
                .build();
    }



}