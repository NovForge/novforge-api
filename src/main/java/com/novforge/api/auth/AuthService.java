package com.novforge.api.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Set<String> adminEmails;

    @Autowired
    public AuthService(@Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder,
                       JwtEncoder jwtEncoder,
                       UserService userService,
                       @Value("${jwt.access-token-expiration:3600}") long expirationSeconds,
                       @Value("${admin.emails:}") String configuredAdminEmails) {
        this.googleJwtDecoder = googleJwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.accessTokenTtl = Duration.ofSeconds(expirationSeconds);
        this.adminEmails = Arrays.stream(configuredAdminEmails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .map(email -> email.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    AuthService(JwtDecoder googleJwtDecoder, JwtEncoder jwtEncoder, UserService userService,
                long expirationSeconds) {
        this(googleJwtDecoder, jwtEncoder, userService, expirationSeconds, "");
    }

    public AuthDto.LoginResponse loginWithGoogle(String idToken) {
        Jwt googleJwt = googleJwtDecoder.decode(idToken);
        UserDto.Response user = userService.getGoogleUser(googleJwt);
        String accessToken = issueAccessToken(user);
        boolean isAdmin = adminEmails.contains(user.userEmail().toLowerCase(Locale.ROOT));
        return new AuthDto.LoginResponse(accessToken, "Bearer", accessTokenTtl.toSeconds(), isAdmin, user);
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
