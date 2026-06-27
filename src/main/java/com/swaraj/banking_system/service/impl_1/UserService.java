package com.swaraj.banking_system.service.impl_1;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;

public interface UserService {

    RegisterResponse registerUser(
            RegisterRequest request
    );
    LoginResponse login(LoginRequest request);
}