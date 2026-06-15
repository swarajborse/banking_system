package com.swaraj.banking_system.service;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;

public interface UserService {

    RegisterResponse registerUser(
            RegisterRequest request
    );
    LoginResponse loginUser(
            LoginRequest request
    );
}