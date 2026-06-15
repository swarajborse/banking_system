package com.swaraj.banking_system.controller;

import com.swaraj.banking_system.dto.request.RegisterRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/test")
public class ValidationTestController {

    @PostMapping("/register")
    public String register(
            @Valid @RequestBody RegisterRequest request) {

        return "Validation Passed";
    }
}