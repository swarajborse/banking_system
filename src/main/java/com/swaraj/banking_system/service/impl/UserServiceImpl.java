package com.swaraj.banking_system.service.impl;

import com.swaraj.banking_system.dto.request.LoginRequest;
import com.swaraj.banking_system.dto.request.RegisterRequest;
import com.swaraj.banking_system.dto.response.LoginResponse;
import com.swaraj.banking_system.dto.response.RegisterResponse;
import com.swaraj.banking_system.entity.User;
import com.swaraj.banking_system.enums.Role;
import com.swaraj.banking_system.enums.UserStatus;
import com.swaraj.banking_system.exception.DuplicateResourceException;
import com.swaraj.banking_system.exception.InvalidLoginException;
import com.swaraj.banking_system.repository.UserRepository;
import com.swaraj.banking_system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                passwordEncoder.encode(
                        request.getPassword()
                )
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
    public LoginResponse loginUser(
            LoginRequest request
    ) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidLoginException(
                                "Invalid email or password"
                        ));

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!matches) {
            throw new InvalidLoginException(
                    "Invalid email or password"
            );
        }

        return new LoginResponse(
                "Login Successful"
        );
    }
}