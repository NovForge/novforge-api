package com.novforge.api.users;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final Set<String> adminEmails;

    public UserService(UserRepository userRepository,
                       @Value("${admin.emails:}") String adminEmails) {
        this.userRepository = userRepository;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .map(email -> email.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public UserDto.Response signupGoogleUser(Jwt googleJwt, String requestedNickname) {
        if (userRepository.findByGoogleUid(googleJwt.getSubject()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 Google 계정입니다.");
        }
        String nickname = normalizeNickname(requestedNickname);
        validateNicknameAvailable(nickname, null);
        User user = userRepository.save(new User(
                googleJwt.getSubject(), requiredClaim(googleJwt, "name"), nickname, requiredClaim(googleJwt, "email"),
                googleJwt.getClaimAsString("picture")));
        return UserDto.Response.from(user);
    }

    public UserDto.Response getGoogleUser(Jwt googleJwt) {
        User user = userRepository.findByGoogleUid(googleJwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "가입되지 않은 Google 계정입니다."));
        return UserDto.Response.from(user);
    }

    public UserDto.Response getMe(Jwt jwt) {
        return UserDto.Response.from(findMe(jwt));
    }

    public List<UserDto.Response> getAllUsers(Jwt jwt) {
        validateAdmin(jwt);
        return userRepository.findAll().stream()
                .map(UserDto.Response::from)
                .toList();
    }

    @Transactional
    public UserDto.Response updateMe(Jwt jwt, UserDto.UpdateRequest request) {
        User user = findMe(jwt);
        String nickname = normalizeNickname(request.userNickname());
        validateNicknameAvailable(nickname, user.getId());
        user.updateNickname(nickname);
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

    private String normalizeNickname(String nickname) {
        return nickname.trim();
    }

    private void validateNicknameAvailable(String nickname, Long currentUserId) {
        userRepository.findByNickname(nickname)
                .filter(user -> currentUserId == null || !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
                });
    }

    private void validateAdmin(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || !adminEmails.contains(email.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 사용자 전체 목록을 조회할 수 있습니다.");
        }
    }
}
