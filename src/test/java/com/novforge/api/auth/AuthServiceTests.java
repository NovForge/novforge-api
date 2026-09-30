package com.novforge.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.novforge.api.users.UserDto;
import com.novforge.api.users.UserService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {
    @Mock JwtDecoder googleJwtDecoder;
    @Mock JwtEncoder jwtEncoder;
    @Mock UserService userService;

    @Test
    void googleLoginReturnsOurAccessTokenAndUser() {
        Jwt googleJwt = jwt("google-token", "google-123");
        Jwt accessJwt = jwt("novforge-access-token", "1");
        UserDto.Response user = new UserDto.Response(
                1L, "홍길동", "노브작가", "user@example.com", null, Instant.now(), null);

        when(googleJwtDecoder.decode("google-id-token")).thenReturn(googleJwt);
        when(userService.getGoogleUser(googleJwt)).thenReturn(user);
        when(jwtEncoder.encode(any())).thenReturn(accessJwt);

        AuthService authService = new AuthService(googleJwtDecoder, jwtEncoder, userService, 3600);
        AuthDto.LoginResponse response = authService.loginWithGoogle("google-id-token");

        assertThat(response.accessToken()).isEqualTo("novforge-access-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.user().userId()).isEqualTo(1L);
    }

    private Jwt jwt(String tokenValue, String subject) {
        return new Jwt(tokenValue, Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"), Map.of("sub", subject));
    }
}
