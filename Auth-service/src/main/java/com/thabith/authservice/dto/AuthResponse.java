package com.thabith.authservice.dto;

public record AuthResponse(
        long id, String email, String hashedPassword, String role
) {
}
