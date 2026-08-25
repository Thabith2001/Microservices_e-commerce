package com.thabith.userservice.dto;

import com.thabith.userservice.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String contact;
    private Role role;
}
