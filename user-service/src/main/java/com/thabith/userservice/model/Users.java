package com.thabith.userservice.model;


import com.thabith.userservice.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private String contact;
    private String profileUri;
    private String profileId;
    @Enumerated(EnumType.STRING)
    private Role role;
}
