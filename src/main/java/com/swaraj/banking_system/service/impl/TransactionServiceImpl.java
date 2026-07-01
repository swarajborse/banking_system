package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.DepositRequest;
import com.swaraj.banking_system.dto.request.TransferRequest;
import com.swaraj.banking_system.dto.request.WithdrawRequest;
import com.swaraj.banking_system.dto.response.TransactionHistoryResponse;
import com.swaraj.banking_system.dto.response.TransactionResponse;
import com.swaraj.banking_system.entity.BankAccount;
import com.swaraj.banking_system.entity.Transaction;
import com.swaraj.banking_system.entity.User;
import com.swaraj.banking_system.enums.AccountStatus;
import com.swaraj.banking_system.enums.TransactionStatus;
import com.swaraj.banking_system.enums.TransactionType;
import com.swaraj.banking_system.exception.AccountNotActiveException;
import com.swaraj.banking_system.exception.AccountNotFoundException;
import com.swaraj.banking_system.exception.UnauthorizedAccessException;
import com.swaraj.banking_system.exception.UserNotFoundException;
import com.swaraj.banking_system.repository.BankAccountRepository;
import com.swaraj.banking_system.repository.TransactionRepository;
import com.swaraj.banking_system.repository.UserRepository;
import com.swaraj.banking_system.service.interfaces.TransactionService;
import com.swaraj.banking_system.util.TransactionReferenceGenerator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private BankAccount getOwnedAccount(Long accountId) {

        User user = getLoggedInUser();

        BankAccount account = bankAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
         new AccountNotFoundException("Account not found."));

        if (!account.getUser().getId().equals(user.getId())) {

            throw new UnauthorizedAccessException(
                    "You are not authorized to access this account."
            );
        }

        return account;
    }

    private void validateActiveAccount(
            BankAccount account
    ) {

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {

            throw new AccountNotActiveException(
                    "Account is not active."
            );
        }

    }

    private TransactionHistoryResponse mapToHistoryResponse(
            Transaction transaction,
            BankAccount account) {

        TransactionHistoryResponse response =
                new TransactionHistoryResponse();

        response.setReferenceNumber(
                transaction.getReferenceNumber()
        );

        response.setTransactionType(
                transaction.getTransactionType()
        );

        response.setTransactionStatus(
                transaction.getTransactionStatus()
        );

        response.setAmount(
                transaction.getAmount()
        );

        response.setDescription(
                transaction.getDescription()
        );

        response.setTransactionTime(
                transaction.getCreatedAt()
        );

        response.setAccountUsed(
                maskAccountNumber(
                        account.getAccountNumber()
                )
        );

        if (transaction.getSenderAccount() != null) {
            response.setSenderAccount(
                    maskAccountNumber(
                            transaction.getSenderAccount()
                                    .getAccountNumber()
                    )
            );
        }

        if (transaction.getReceiverAccount() != null) {
            response.setReceiverAccount(
                    maskAccountNumber(
                            transaction.getReceiverAccount()
                                    .getAccountNumber()
                    )
            );
        }

        // Human readable title
        switch (transaction.getTransactionType()) {

            case DEPOSIT ->
                    response.setTitle("Money Deposited");

            case WITHDRAW ->
                    response.setTitle("Cash Withdrawal");

            case TRANSFER ->
                    response.setTitle("Money Transfer");
        }

        return response;
    }

    private String maskAccountNumber(
            String accountNumber) {

        if (accountNumber == null) {
            return null;
        }

        return "XXXXXX" +
                accountNumber.substring(
                        accountNumber.length() - 4
                );
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


    @Transactional
    @Override
    public TransactionResponse transfer(TransferRequest request) {

        // 1. Get sender account (also validates ownership)
        BankAccount senderAccount =
                getOwnedAccount(request.getFromAccountId());

        // 2. Validate sender account
        validateActiveAccount(senderAccount);

        // 3. Find receiver account
        BankAccount receiverAccount =
                bankAccountRepository
                        .findById(request.getToAccountId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Receiver account not found."
                                ));

        // 4. Validate receiver account
        validateActiveAccount(receiverAccount);

        // 5. Prevent self-transfer
        if (senderAccount.getAccountId()
                .equals(receiverAccount.getAccountId())) {

            throw new RuntimeException(
                    "Cannot transfer to the same account."
            );
        }

        // 6. Check sufficient balance
        if (senderAccount.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new RuntimeException(
                    "Insufficient balance."
            );
        }

        // 7. Debit sender
        senderAccount.setBalance(
                senderAccount.getBalance()
                        .subtract(request.getAmount())
        );

        // 8. Credit receiver
        receiverAccount.setBalance(
                receiverAccount.getBalance()
                        .add(request.getAmount())
        );

        // 9. Save both accounts
        bankAccountRepository.save(senderAccount);
        bankAccountRepository.save(receiverAccount);

        // 10. Create transaction
        Transaction transaction =
                Transaction.builder()
                        .referenceNumber(
                                TransactionReferenceGenerator.generate()
                        )
                        .transactionType(
                                TransactionType.TRANSFER
                        )
                        .transactionStatus(
                                TransactionStatus.SUCCESS
                        )
                        .amount(request.getAmount())
                        .description(request.getDescription())
                        .createdAt(LocalDateTime.now())
                        .senderAccount(senderAccount)
                        .receiverAccount(receiverAccount)
                        .build();

        // 11. Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 12. Return response
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
                .description(savedTransaction.getDescription())
                .createdAt(savedTransaction.getCreatedAt())
                .senderAccountNumber(
                        senderAccount.getAccountNumber()
                )
                .receiverAccountNumber(
                        receiverAccount.getAccountNumber()
                )
                .build();
    }

    @Override
    public Page<TransactionHistoryResponse> getTransactionHistory(
            Long accountId,
            int page,
            int size
    ) {

        BankAccount account =
                getOwnedAccount(accountId);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<Transaction> transactions =
                transactionRepository
                        .findBySenderAccountOrReceiverAccountOrderByCreatedAtDesc(
                                account,
                                account,
                                pageable
                        );

        return transactions.map(
                transaction ->
                        mapToHistoryResponse(
                                transaction,
                                account
                        )
        );
    }




}