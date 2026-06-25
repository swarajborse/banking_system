package com.swaraj.banking_system.dto.request;

import com.swaraj.banking_system.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    @NotBlank(message = "IFSC Code is required")
    private String ifscCode;

    @PositiveOrZero(message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;
}