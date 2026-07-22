package com.novforge.api.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(300),
                Map.of("alg", "RS256"),
                Map.of("sub", "google-123", "name", "홍길동", "email", "user@example.com",
                        "picture", "https://example.com/profile.jpg"));
    }

    @Test
    void joinCreatesAUserFromGoogleClaims() {
        when(userRepository.findByGoogleUid("google-123")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDto.Response response = userService.joinGoogleUser(jwt);

        assertThat(response.userName()).isEqualTo("홍길동");
        assertThat(response.userEmail()).isEqualTo("user@example.com");
        assertThat(response.profileImage()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    void updateMeOnlyChangesTheCurrentUsersName() {
        User user = new User("google-123", "이전 이름", "user@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        jwt = serviceJwt("1");

        UserDto.Response response = userService.updateMe(jwt, new UserDto.UpdateRequest("새 이름"));

        assertThat(response.userName()).isEqualTo("새 이름");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void withdrawDeletesTheAuthenticatedUser() {
        User user = new User("google-123", "홍길동", "user@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        jwt = serviceJwt("1");

        userService.withdraw(jwt);

        verify(userRepository).delete(user);
    }

    private Jwt serviceJwt(String subject) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(300),
                Map.of("alg", "HS256"), Map.of("sub", subject));
    }
}
