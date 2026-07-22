package com.novforge.api.users;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto.Response joinGoogleUser(Jwt googleJwt) {
        User user = userRepository.findByGoogleUid(googleJwt.getSubject())
                .orElseGet(() -> userRepository.save(new User(
                        googleJwt.getSubject(), requiredClaim(googleJwt, "name"), requiredClaim(googleJwt, "email"),
                        googleJwt.getClaimAsString("picture"))));
        return UserDto.Response.from(user);
    }

    public UserDto.Response getMe(Jwt jwt) {
        return UserDto.Response.from(findMe(jwt));
    }

    @Transactional
    public UserDto.Response updateMe(Jwt jwt, UserDto.UpdateRequest request) {
        User user = findMe(jwt);
        user.updateName(request.userName().trim());
        return UserDto.Response.from(user);
    }

    @Transactional
    public UserDto.Response updateProfileImage(Jwt jwt, UserDto.ProfileImageRequest request) {
        User user = findMe(jwt);
        user.updateProfileImage(request.profileImage().trim());
        return UserDto.Response.from(user);
    }

    @Transactional
    public void withdraw(Jwt jwt) {
        userRepository.delete(findMe(jwt));
    }

    private User findMe(Jwt jwt) {
        Long userId;
        try {
            userId = Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "올바르지 않은 액세스 토큰입니다.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가입된 사용자가 아닙니다."));
    }

    private String requiredClaim(Jwt jwt, String claim) {
        String value = jwt.getClaimAsString(claim);
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google 계정에 " + claim + " 정보가 없습니다.");
        }
        return value;
    }
}
