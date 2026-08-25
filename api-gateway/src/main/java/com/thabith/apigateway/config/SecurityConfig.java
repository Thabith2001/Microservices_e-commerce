package com.thabith.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http

                .csrf(
                        ServerHttpSecurity.CsrfSpec::disable
                )

                .authorizeExchange(exchange -> exchange

                        .pathMatchers(
                                "/auth-service/api/v1/auth/signin","/auth-service/api/v1/auth/register").permitAll()

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/products"
                        ).permitAll()
                        .anyExchange()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults())
                )

                .build();
    }
}
