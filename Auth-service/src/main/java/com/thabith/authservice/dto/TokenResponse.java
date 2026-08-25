package com.thabith.authservice.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
