package com.riwi.order_management.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.order_management.dto.request.UserCreateRequest;
import com.riwi.order_management.dto.response.UserResponse;
import com.riwi.order_management.entity.User;
import com.riwi.order_management.entity.UserRole;
import com.riwi.order_management.exception.BusinessException;
import com.riwi.order_management.exception.ResourceNotFoundException;
import com.riwi.order_management.mapper.UserMapper;
import com.riwi.order_management.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with encrypted password.
     * 
     * @param request the user creation data
     * @return the created user response
     */
    public UserResponse createUser(UserCreateRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER) // Default role is USER
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * Gets all users (Admin only).
     * 
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Gets a user by ID.
     * 
     * @param id the user ID
     * @return the user response
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    /**
     * Gets a user by email.
     * 
     * @param email the user email
     * @return the user response
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    /**
     * Gets the user entity by ID (internal use).
     * 
     * @param id the user ID
     * @return the user entity
     */
    @Transactional(readOnly = true)
    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Promotes a user to admin role (Admin only).
     * 
     * @param id the user ID to promote
     * @return the updated user response
     */
    public UserResponse promoteToAdmin(UUID id) {
        User user = getUserEntityById(id);
        user.setRole(UserRole.ADMIN);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
