package com.thabith.authservice.controllers;

import com.thabith.authservice.dto.CredRequest;
import com.thabith.authservice.dto.TokenResponse;
import com.thabith.authservice.dto.UserRequest;
import com.thabith.authservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JsonMapper jsonMapper;

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signIn(@RequestBody CredRequest request) {
        TokenResponse resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/register")
    public ResponseEntity<TokenResponse> signUp(@RequestPart("user") String user,
                                                @RequestPart(value = "profile",
                                                        required = false) MultipartFile multipartFile) {
        UserRequest users = jsonMapper.readValue(user, UserRequest.class);
        TokenResponse resp = authService.register(users, multipartFile);
        return ResponseEntity.ok(resp);
    }
}
