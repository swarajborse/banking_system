package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.DepositRequest;
import com.swaraj.banking_system.dto.request.WithdrawRequest;
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

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private BankAccount getOwnedAccount(Long accountId) {

        User user = getLoggedInUser();

        BankAccount account = bankAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not authorized to access this account."
            );
        }

        return account;
    }

    private void validateActiveAccount(
            BankAccount account
    ) {

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {

            throw new RuntimeException(
                    "Account is not active."
            );
        }

    }
    @Override
    public TransactionResponse deposit(DepositRequest request) {

       BankAccount account = getOwnedAccount(request.getAccountId());

validateActiveAccount(account);

account.setBalance(
        account.getBalance().add(request.getAmount())
);

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

    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {

        // Get logged-in user's account
        BankAccount account =
                getOwnedAccount(request.getAccountId());

        // Check account status
        validateActiveAccount(account);

        // Check sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {

            throw new RuntimeException(
                    "Insufficient Balance."
            );
        }

        // Update balance
        account.setBalance(
                account.getBalance()
                        .subtract(request.getAmount())
        );

        // Save account
        bankAccountRepository.save(account);

        // Create transaction
        Transaction transaction =
                Transaction.builder()
                        .referenceNumber(
                                TransactionReferenceGenerator.generate()
                        )
                        .transactionType(
                                TransactionType.WITHDRAW
                        )
                        .transactionStatus(
                                TransactionStatus.SUCCESS
                        )
                        .amount(request.getAmount())
                        .description(
                                request.getDescription()
                        )
                        .createdAt(LocalDateTime.now())
                        .senderAccount(account)
                        .build();

        // Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // Return response
        return TransactionResponse.builder()
                .referenceNumber(
                        savedTransaction.getReferenceNumber()
                )
                .transactionType(
                        savedTransaction.getTransactionType()
                )
                .transactionStatus(
                        savedTransaction.getTransactionStatus()
                )
                .amount(savedTransaction.getAmount())
                .description(
                        savedTransaction.getDescription()
                )
                .createdAt(
                        savedTransaction.getCreatedAt()
                )
                .senderAccountNumber(
                        account.getAccountNumber()
                )
                .receiverAccountNumber(null)
                .build();
    }


}