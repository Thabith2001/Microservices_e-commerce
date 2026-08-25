package com.thabith.userservice.dto;

import com.thabith.userservice.enums.Role;

public record AuthResponse(
        long id, String email, String hashedPassword, Role role
) {
}
