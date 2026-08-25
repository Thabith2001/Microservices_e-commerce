package com.thabith.userservice.dto;


import com.thabith.userservice.enums.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String contact;
    private String profileUri;
    private Role role;
}
