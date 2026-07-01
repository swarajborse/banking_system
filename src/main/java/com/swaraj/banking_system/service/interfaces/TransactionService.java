package com.swaraj.banking_system.service.interfaces;

import com.swaraj.banking_system.dto.request.DepositRequest;
import com.swaraj.banking_system.dto.request.TransferRequest;
import com.swaraj.banking_system.dto.request.WithdrawRequest;
import com.swaraj.banking_system.dto.response.TransactionHistoryResponse;
import com.swaraj.banking_system.dto.response.TransactionResponse;
import org.hibernate.query.Page;

import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);



    TransactionResponse withdraw(WithdrawRequest request);

    TransactionResponse transfer(TransferRequest request);


    Page<TransactionHistoryResponse> getTransactionHistory(
            Long accountId,
            int page,
            int size
    );
}
