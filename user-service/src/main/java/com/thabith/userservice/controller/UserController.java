package com.thabith.userservice.controller;

import com.thabith.userservice.dto.AuthResponse;
import com.thabith.userservice.dto.UserRequest;
import com.thabith.userservice.dto.UserResponse;
import com.thabith.userservice.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Objects;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UsersService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/register")
    public ResponseEntity<UserResponse> saveUser(@RequestPart("user") UserRequest user, @RequestPart(value = "profile", required = false) MultipartFile multipartFile) {
        UserResponse response = service.addUser(user, multipartFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfilePic(@AuthenticationPrincipal Jwt jwt, @RequestParam("profile") MultipartFile multipartFile) {

        UserResponse response = service.updateProfile(Long.parseLong(Objects.requireNonNull(jwt.getSubject())), multipartFile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> credentials(@RequestParam("email") String email) {
        AuthResponse auth = service.getUserCredentials(email);
        return ResponseEntity.ok(auth);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        String resp = service.delete(Long.parseLong(Objects.requireNonNull(jwt.getSubject())));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resp);
    }

}

