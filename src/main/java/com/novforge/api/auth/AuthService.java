package com.novforge.api.auth;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.novforge.api.users.UserDto;
import com.novforge.api.users.UserService;

@Service
public class AuthService {
    private final JwtDecoder googleJwtDecoder;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final Duration accessTokenTtl;

    public AuthService(@Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder,
                       JwtEncoder jwtEncoder,
                       UserService userService,
                       @Value("${jwt.access-token-expiration:3600}") long expirationSeconds) {
        this.googleJwtDecoder = googleJwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.accessTokenTtl = Duration.ofSeconds(expirationSeconds);
    }

    public AuthDto.LoginResponse loginWithGoogle(String idToken) {
        Jwt googleJwt = googleJwtDecoder.decode(idToken);
        UserDto.Response user = userService.joinGoogleUser(googleJwt);
        String accessToken = issueAccessToken(user);
        return new AuthDto.LoginResponse(accessToken, "Bearer", accessTokenTtl.toSeconds(), user);
    }

    private String issueAccessToken(UserDto.Response user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("novforge-api")
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenTtl))
                .subject(user.userId().toString())
                .claim("email", user.userEmail())
                .build();
        JwsHeader header = JwsHeader.with(() -> JwsAlgorithms.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
