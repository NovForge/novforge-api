package com.novforge.api.auth;

import jakarta.validation.constraints.NotBlank;

import com.novforge.api.users.UserDto;

public final class AuthDto {
    private AuthDto() {}

    public record GoogleLoginRequest(
            @NotBlank(message = "Google ID 토큰은 필수입니다.") String idToken) {}

    public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresIn,
            boolean isAdmin,
            UserDto.Response user) {}
}
