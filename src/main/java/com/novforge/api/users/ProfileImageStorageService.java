package com.novforge.api.users;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileImageStorageService {
    private static final Logger log = LoggerFactory.getLogger(ProfileImageStorageService.class);
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private final HttpClient httpClient;
    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;

    @Autowired
    public ProfileImageStorageService(
            @Value("${supabase.url:}") String supabaseUrl,
            @Value("${supabase.secret-key:}") String secretKey,
            @Value("${supabase.profile-bucket:profiles}") String bucket) {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(), supabaseUrl, secretKey, bucket);
    }

    ProfileImageStorageService(HttpClient httpClient, String supabaseUrl, String secretKey, String bucket) {
        this.httpClient = httpClient;
        this.supabaseUrl = stripTrailingSlash(supabaseUrl);
        this.secretKey = secretKey.trim();
        this.bucket = bucket.trim();
    }

    public StoredProfileImage upload(Long userId, MultipartFile file) {
        ensureConfigured();
        byte[] bytes = readAndValidate(file);
        ImageType imageType = detectImageType(bytes);
        String objectPath = "users/" + userId + "/" + UUID.randomUUID() + "." + imageType.extension();
        URI uploadUri = URI.create(supabaseUrl + "/storage/v1/object/" + bucket + "/" + objectPath);
        HttpRequest request = authorizedRequest(uploadUri)
                .header("Content-Type", imageType.contentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();
        sendExpectSuccess(request, "프로필 이미지를 저장소에 업로드하지 못했습니다.");
        String publicUrl = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + objectPath;
        return new StoredProfileImage(publicUrl, objectPath);
    }

    public void deletePreviousImage(String imageUrl) {
        String objectPath = objectPathFromPublicUrl(imageUrl);
        if (objectPath == null) return;
        HttpRequest request = authorizedRequest(URI.create(supabaseUrl + "/storage/v1/object/" + bucket + "/" + objectPath))
                .DELETE()
                .build();
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("Failed to delete previous profile image from Supabase Storage: status={}", response.statusCode());
            }
        } catch (IOException exception) {
            log.warn("Failed to delete previous profile image from Supabase Storage", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while deleting previous profile image", exception);
        }
    }

    private HttpRequest.Builder authorizedRequest(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("apikey", secretKey);
        if (!secretKey.startsWith("sb_secret_")) {
            builder.header("Authorization", "Bearer " + secretKey);
        }
        return builder;
    }

    private void sendExpectSuccess(HttpRequest request, String failureMessage) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Supabase Storage upload failed: status={}, body={}", response.statusCode(), response.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, failureMessage);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, failureMessage, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, failureMessage, exception);
        }
    }

    private byte[] readAndValidate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 이미지 파일은 필수입니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, "프로필 이미지는 5MB 이하여야 합니다.");
        }
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 이미지 파일을 읽지 못했습니다.", exception);
        }
    }

    private ImageType detectImageType(byte[] bytes) {
        if (bytes.length >= 3 && unsigned(bytes[0]) == 0xFF && unsigned(bytes[1]) == 0xD8 && unsigned(bytes[2]) == 0xFF) {
            return new ImageType("jpg", "image/jpeg");
        }
        if (bytes.length >= 8 && unsigned(bytes[0]) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G'
                && unsigned(bytes[4]) == 0x0D && unsigned(bytes[5]) == 0x0A && unsigned(bytes[6]) == 0x1A && unsigned(bytes[7]) == 0x0A) {
            return new ImageType("png", "image/png");
        }
        if (bytes.length >= 12 && ascii(bytes, 0, "RIFF") && ascii(bytes, 8, "WEBP")) {
            return new ImageType("webp", "image/webp");
        }
        throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "JPEG, PNG 또는 WebP 이미지만 업로드할 수 있습니다.");
    }

    private void ensureConfigured() {
        if (supabaseUrl.isBlank() || secretKey.isBlank() || !bucket.matches("[A-Za-z0-9._-]+")) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "프로필 이미지 저장소가 설정되지 않았습니다.");
        }
    }

    private String objectPathFromPublicUrl(String imageUrl) {
        if (imageUrl == null || supabaseUrl.isBlank()) return null;
        String prefix = supabaseUrl + "/storage/v1/object/public/" + bucket + "/";
        return imageUrl.startsWith(prefix) ? imageUrl.substring(prefix.length()) : null;
    }

    private static boolean ascii(byte[] bytes, int offset, String expected) {
        for (int index = 0; index < expected.length(); index++) {
            if (bytes[offset + index] != expected.charAt(index)) return false;
        }
        return true;
    }

    private static int unsigned(byte value) { return value & 0xFF; }
    private static String stripTrailingSlash(String value) { return value.trim().replaceAll("/+$", ""); }

    public record StoredProfileImage(String publicUrl, String objectPath) {}
    private record ImageType(String extension, String contentType) {}
}
