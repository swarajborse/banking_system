package com.swaraj.banking_system.controller;

import com.swaraj.banking_system.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {

        throw new ResourceNotFoundException(
                "User not found"
        );
    }
}