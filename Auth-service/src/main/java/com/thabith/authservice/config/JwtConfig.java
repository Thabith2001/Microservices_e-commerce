package com.thabith.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Value("classpath:keys/public.pem")
    private Resource publicKey;

    @Value("classpath:keys/private.pem")
    private Resource privateKey;


    @Bean
    public JwtEncoder jwtEncoder() throws IOException {

        try (
                InputStream publicStream = publicKey.getInputStream();
                InputStream privateStream = privateKey.getInputStream()
        ) {

            RSAPublicKey rsaPublicKey =
                    RsaKeyConverters.x509()
                            .convert(publicStream);

            RSAPrivateKey rsaPrivateKey =
                    RsaKeyConverters.pkcs8()
                            .convert(privateStream);

            return NimbusJwtEncoder
                    .withKeyPair(
                            rsaPublicKey,
                            rsaPrivateKey
                    )
                    .build();
        }
    }


    @Bean
    public JwtDecoder jwtDecoder() throws IOException {

        try (InputStream publicStream =
                     publicKey.getInputStream()) {

            RSAPublicKey rsaPublicKey =
                    RsaKeyConverters.x509()
                            .convert(publicStream);

            return NimbusJwtDecoder
                    .withPublicKey(rsaPublicKey)
                    .build();
        }
    }
}