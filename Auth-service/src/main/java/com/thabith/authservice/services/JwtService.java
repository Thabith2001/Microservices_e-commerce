package com.thabith.authservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public String generateToken(
            Long userId,
            String email,
            String role
    ) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60 * 60))
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(
                        header,
                        claims
                );

        return jwtEncoder
                .encode(parameters)
                .getTokenValue();
    }
    
}