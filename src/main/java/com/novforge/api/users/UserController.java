package com.novforge.api.users;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtDecoder googleJwtDecoder;

    public UserController(UserService userService,
                          @Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder) {
        this.userService = userService;
        this.googleJwtDecoder = googleJwtDecoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto.Response signup(@Valid @RequestBody UserDto.SignupRequest request) {
        Jwt googleJwt = googleJwtDecoder.decode(request.idToken());
        return userService.signupGoogleUser(googleJwt);
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
        userService.withdraw(jwt);
    }

    @PatchMapping("/profile-images")
    public UserDto.Response updateProfileImage(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody UserDto.ProfileImageRequest request) {
        return userService.updateProfileImage(jwt, request);
    }
}
