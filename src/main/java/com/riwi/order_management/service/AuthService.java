package com.riwi.order_management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.order_management.dto.request.LoginRequest;
import com.riwi.order_management.dto.request.UserCreateRequest;
import com.riwi.order_management.dto.response.AuthResponse;
import com.riwi.order_management.entity.User;
import com.riwi.order_management.entity.UserRole;
import com.riwi.order_management.exception.BusinessException;
import com.riwi.order_management.repository.UserRepository;
import com.riwi.order_management.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Registers a new user and returns an authentication response with JWT token.
     */
    public AuthResponse register(UserCreateRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        // Create new user with encrypted password
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER) // Default role is USER
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser);

        return AuthResponse.of(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole());
    }

    /**
     * Authenticates user credentials and returns an authentication response with
     * JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user from database
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("User not found"));

            // Generate JWT token
            String token = tokenProvider.generateToken(user);

            return AuthResponse.of(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole());
        } catch (BadCredentialsException e) {
            throw new BusinessException("Invalid email or password");
        }
    }
}
