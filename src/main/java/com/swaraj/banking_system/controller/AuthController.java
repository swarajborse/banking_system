package com.swaraj.banking_system.controller;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;
import com.swaraj.banking_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        return userService.registerUser(
                request
        );
    }

    @PostMapping("/login")
    public LoginResponse loginUser(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        return userService.loginUser(
                request
        );
    }
}