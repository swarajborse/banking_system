package com.swaraj.banking_system.service.impl_1;

import com.swaraj.banking_system.dto.request.CreateAccountRequest;
import com.swaraj.banking_system.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);
    List<AccountResponse> getMyAccounts();

}
