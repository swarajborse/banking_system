package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;
import com.swaraj.banking_system.entity.User;
import com.swaraj.banking_system.enums.Role;
import com.swaraj.banking_system.enums.UserStatus;
import com.swaraj.banking_system.exception.DuplicateResourceException;
import com.swaraj.banking_system.repository.UserRepository;
import com.swaraj.banking_system.service.UserService;
import com.swaraj.banking_system.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTUtil jwtUtil,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RegisterResponse registerUser(
            RegisterRequest request
    ) {

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already registered"
            );
        }

        if (userRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {

            throw new DuplicateResourceException(
                    "Phone number already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());

        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhoneNumber(
                request.getPhoneNumber()
        );

        user.setRole(Role.USER);

        user.setStatus(UserStatus.ACTIVE);

        User savedUser =
                userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();


        String token =
                jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}