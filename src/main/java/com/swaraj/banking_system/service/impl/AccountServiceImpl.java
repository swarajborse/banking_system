package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.CreateAccountRequest;
import com.swaraj.banking_system.dto.response.AccountResponse;
import com.swaraj.banking_system.entity.BankAccount;
import com.swaraj.banking_system.entity.User;
import com.swaraj.banking_system.enums.AccountStatus;
import com.swaraj.banking_system.repository.BankAccountRepository;
import com.swaraj.banking_system.repository.UserRepository;
import com.swaraj.banking_system.service.interfaces.AccountService;
import com.swaraj.banking_system.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(
            BankAccountRepository bankAccountRepository,
            UserRepository userRepository
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {

        // 1. Get logged-in user's email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        // 2. Fetch User from database
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Generate unique account number
        String accountNumber;

        do {
            accountNumber = AccountNumberGenerator.generate();
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));

        // 4. Create BankAccount entity
        BankAccount account = BankAccount.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .accountStatus(AccountStatus.ACTIVE)
                .branchName(request.getBranchName())
                .ifscCode(request.getIfscCode())
                .balance(request.getInitialDeposit())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // 5. Save account
        BankAccount savedAccount =
                bankAccountRepository.save(account);

        // 6. Prepare response
        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getAccountType(),
                savedAccount.getAccountStatus(),
                savedAccount.getBranchName(),
                savedAccount.getIfscCode(),
                savedAccount.getBalance()
        );
    }

    @Override
    public List<AccountResponse> getMyAccounts() {

        // 1. Get logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // 2. Fetch user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Fetch all accounts of the user
        List<BankAccount> accounts =
                bankAccountRepository.findByUserId(user.getId());

        // 4. Convert Entity List -> DTO List
        return accounts.stream()
                .map(account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getAccountStatus(),
                        account.getBranchName(),
                        account.getIfscCode(),
                        account.getBalance()
                ))
                .toList();
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {

        // Get logged-in user's email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Fetch logged-in user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Fetch account
        BankAccount account = bankAccountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        // Ownership validation
        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to access this account."
            );
        }

        // Convert Entity -> DTO
        return new AccountResponse(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getAccountStatus(),
                account.getBranchName(),
                account.getIfscCode(),
                account.getBalance()
        );
    }
}