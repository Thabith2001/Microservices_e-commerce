package com.thabith.authservice.services;

import com.thabith.authservice.client.AuthClient;
import com.thabith.authservice.dto.AuthResponse;
import com.thabith.authservice.dto.CredRequest;
import com.thabith.authservice.dto.TokenResponse;
import com.thabith.authservice.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient client;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public TokenResponse login(CredRequest request) {
        AuthResponse resp = client.credentials(request.getEmail());
        if (!encoder.matches(request.getPassword(), resp.hashedPassword())) {
            throw new BadCredentialsException("INVALID EMAIL OR PASSWORD");
        }
        String token = jwtService.generateToken(
                resp.id(),
                resp.email(),
                resp.role()
        );

        return new TokenResponse(
                token,
                "Bearer",
                3600
        );
    }


    public TokenResponse register(UserRequest userRequest, MultipartFile image) {
        userRequest.setPassword(encoder.encode(userRequest.getPassword()));
        AuthResponse resp = client.saveUser(userRequest, image);
        if (resp == null) {
            throw new NoSuchElementException("ACCOUNT ISN'T CREATED");
        }


        String token = jwtService.generateToken(
                resp.id(),
                resp.email(),
                resp.role()
        );
        return new TokenResponse(token, "Bearer", 3600);
    }
}
