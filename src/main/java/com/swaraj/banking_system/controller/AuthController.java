package com.swaraj.banking_system.controller;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;
import com.swaraj.banking_system.service.interfaces.UserService;
import com.swaraj.banking_system.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                userService.login(request)
        );
    }


}

