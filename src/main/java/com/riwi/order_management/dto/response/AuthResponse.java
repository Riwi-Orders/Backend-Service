package com.riwi.order_management.dto.response;

import java.util.UUID;

import com.riwi.order_management.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private UUID userId;
    private String email;
    private String name;
    private UserRole role;

    public static AuthResponse of(String token, UUID userId, String email, String name, UserRole role) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(userId)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}
