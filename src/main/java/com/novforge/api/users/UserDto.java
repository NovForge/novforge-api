package com.novforge.api.users;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserDto {
    private UserDto() {}

    public record SignupRequest(
            @NotBlank(message = "Google ID 토큰은 필수입니다.")
            String idToken,
            @NotBlank(message = "사용자 닉네임은 필수입니다.")
            @Size(max = 50, message = "사용자 닉네임은 50자 이하여야 합니다.")
            String userNickname) {}

    public record Response(
            Long userId,
            String userName,
            String userNickname,
            String userEmail,
            String profileImage,
            Instant createdAt,
            Instant updatedAt) {
        static Response from(User user) {
            return new Response(user.getId(), user.getName(), user.getNickname(), user.getEmail(), user.getProfileImage(),
                    user.getCreatedAt(), user.getUpdatedAt());
        }
    }

    public record UpdateRequest(
            @NotBlank(message = "사용자 닉네임은 비어 있을 수 없습니다.")
            @Size(max = 50, message = "사용자 닉네임은 50자 이하여야 합니다.")
            String userNickname) {}

    public record ProfileImageRequest(
            @NotBlank(message = "프로필 이미지 URL은 비어 있을 수 없습니다.")
            @Size(max = 512, message = "프로필 이미지 URL은 512자 이하여야 합니다.")
            String profileImage) {}
}
