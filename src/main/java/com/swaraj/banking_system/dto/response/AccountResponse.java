package com.swaraj.banking_system.dto.response;

import com.swaraj.banking_system.enums.AccountStatus;
import com.swaraj.banking_system.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;

    private AccountType accountType;

    private AccountStatus accountStatus;

    private String branchName;

    private String ifscCode;

    private BigDecimal balance;
}