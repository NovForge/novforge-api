package com.novforge.api.users;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtDecoder googleJwtDecoder;
    private final ProfileImageStorageService profileImageStorageService;

    public UserController(UserService userService,
                          @Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder,
                          ProfileImageStorageService profileImageStorageService) {
        this.userService = userService;
        this.googleJwtDecoder = googleJwtDecoder;
        this.profileImageStorageService = profileImageStorageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.Response signup(@Valid @RequestBody UserDto.SignupRequest request) {
        Jwt googleJwt = googleJwtDecoder.decode(request.idToken());
        return userService.signupGoogleUser(googleJwt, request.userNickname());
    }

    @GetMapping
    public List<UserDto.Response> getUsers(@AuthenticationPrincipal Jwt jwt) {
        return userService.getAllUsers(jwt);
    }

    @GetMapping("/me")
    public UserDto.Response getMe(@AuthenticationPrincipal Jwt jwt) {
        return userService.getMe(jwt);
    }

    @PatchMapping("/me")
    public UserDto.Response updateMe(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody UserDto.UpdateRequest request) {
        return userService.updateMe(jwt, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@AuthenticationPrincipal Jwt jwt) {
        String profileImage = userService.getMe(jwt).profileImage();
        userService.withdraw(jwt);
        profileImageStorageService.deletePreviousImage(profileImage);
    }

    @PatchMapping(value = "/profile-images", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto.Response updateProfileImage(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody UserDto.ProfileImageRequest request) {
        return userService.updateProfileImage(jwt, request);
    }

    @PatchMapping(value = "/profile-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDto.Response uploadProfileImage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("profileImage") MultipartFile profileImage) {
        UserDto.Response currentUser = userService.getMe(jwt);
        ProfileImageStorageService.StoredProfileImage stored =
                profileImageStorageService.upload(currentUser.userId(), profileImage);
        UserDto.Response updatedUser = userService.updateProfileImage(
                jwt, new UserDto.ProfileImageRequest(stored.publicUrl()));
        profileImageStorageService.deletePreviousImage(currentUser.profileImage());
        return updatedUser;
    }
}
