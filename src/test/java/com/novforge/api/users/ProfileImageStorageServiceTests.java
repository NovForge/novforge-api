package com.novforge.api.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

class ProfileImageStorageServiceTests {

    @Test
    void uploadsValidatedJpegAndReturnsPublicUrl() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);
        ProfileImageStorageService service = new ProfileImageStorageService(
                httpClient, "https://project.supabase.co/", "secret", "profiles");
        MockMultipartFile file = new MockMultipartFile(
                "profileImage", "avatar.jpg", "image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00});

        ProfileImageStorageService.StoredProfileImage stored = service.upload(7L, file);

        assertThat(stored.publicUrl()).startsWith(
                "https://project.supabase.co/storage/v1/object/public/profiles/users/7/");
        assertThat(stored.publicUrl()).endsWith(".jpg");
    }

    @Test
    void rejectsFileWhoseBytesAreNotAnAllowedImage() {
        ProfileImageStorageService service = new ProfileImageStorageService(
                mock(HttpClient.class), "https://project.supabase.co", "secret", "profiles");
        MockMultipartFile file = new MockMultipartFile(
                "profileImage", "avatar.jpg", "image/jpeg", "not-an-image".getBytes());

        assertThatThrownBy(() -> service.upload(1L, file))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("JPEG, PNG 또는 WebP");
    }

    @Test
    void rejectsFileLargerThanFiveMegabytes() {
        ProfileImageStorageService service = new ProfileImageStorageService(
                mock(HttpClient.class), "https://project.supabase.co", "secret", "profiles");
        MockMultipartFile file = new MockMultipartFile(
                "profileImage", "avatar.jpg", "image/jpeg", new byte[5 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> service.upload(1L, file))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("5MB 이하");
    }

    @Test
    void rejectsUploadWhenStorageIsNotConfigured() {
        ProfileImageStorageService service = new ProfileImageStorageService(
                mock(HttpClient.class), "", "", "profiles");
        MockMultipartFile file = new MockMultipartFile(
                "profileImage", "avatar.jpg", "image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});

        assertThatThrownBy(() -> service.upload(1L, file))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("저장소가 설정되지 않았습니다");
    }
}
