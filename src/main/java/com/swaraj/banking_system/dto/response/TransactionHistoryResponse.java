package com.swaraj.banking_system.dto.response;

import com.swaraj.banking_system.enums.TransactionStatus;
import com.swaraj.banking_system.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponse {

    private String referenceNumber;

    private String title;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private BigDecimal amount;

    private String description;

    private LocalDateTime transactionTime;

    private String accountUsed;

    private String senderAccount;

    private String receiverAccount;
}