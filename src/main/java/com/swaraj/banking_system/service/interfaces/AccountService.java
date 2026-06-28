package com.swaraj.banking_system.service.interfaces;

import com.swaraj.banking_system.dto.request.CreateAccountRequest;
import com.swaraj.banking_system.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);
    List<AccountResponse> getMyAccounts();
    AccountResponse getAccountById(Long accountId);

}
