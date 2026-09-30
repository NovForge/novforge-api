package com.novforge.api.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

class SecurityConfigTests {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void configuredAdminEmailReceivesAdminRole() {
        JwtAuthenticationConverter converter =
                securityConfig.jwtAuthenticationConverter(" first@example.com, ADMIN@example.com ");

        assertThat(converter.convert(jwt("admin@example.com")).getAuthorities())
                .extracting("authority")
                .contains("ROLE_ADMIN");
    }

    @Test
    void regularUserDoesNotReceiveAdminRole() {
        JwtAuthenticationConverter converter =
                securityConfig.jwtAuthenticationConverter("admin@example.com");

        assertThat(converter.convert(jwt("user@example.com")).getAuthorities())
                .extracting("authority")
                .doesNotContain("ROLE_ADMIN");
    }

    private Jwt jwt(String email) {
        Instant issuedAt = Instant.now();
        return Jwt.withTokenValue("access-token")
                .header("alg", "HS256")
                .subject("1")
                .claim("email", email)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .build();
    }
}
