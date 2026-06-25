package com.swaraj.banking_system.service.impl_1;

import com.swaraj.banking_system.dto.request.CreateAccountRequest;
import com.swaraj.banking_system.dto.response.AccountResponse;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

}
