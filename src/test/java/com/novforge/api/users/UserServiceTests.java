package com.novforge.api.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, "admin@example.com");
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

        when(userRepository.findByNickname("노브작가")).thenReturn(Optional.empty());

        UserDto.Response response = userService.signupGoogleUser(jwt, " 노브작가 ");

        assertThat(response.userName()).isEqualTo("홍길동");
        assertThat(response.userNickname()).isEqualTo("노브작가");
        assertThat(response.userEmail()).isEqualTo("user@example.com");
        assertThat(response.profileImage()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    void updateMeOnlyChangesTheCurrentUsersNickname() {
        User user = new User("google-123", "Google 이름", "이전닉네임", "user@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByNickname("새닉네임")).thenReturn(Optional.empty());

        jwt = serviceJwt("1");

        UserDto.Response response = userService.updateMe(jwt, new UserDto.UpdateRequest(" 새닉네임 "));

        assertThat(response.userNickname()).isEqualTo("새닉네임");
        assertThat(response.userName()).isEqualTo("Google 이름");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void signupRejectsAnExistingNickname() {
        User existingUser = new User(
                "google-456", "다른 사용자", "노브작가", "other@example.com", null);
        when(userRepository.findByGoogleUid("google-123")).thenReturn(Optional.empty());
        when(userRepository.findByNickname("노브작가")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.signupGoogleUser(jwt, "노브작가"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void updateRejectsAnotherUsersNickname() {
        User currentUser = new User(
                "google-123", "Google 이름", "현재닉네임", "user@example.com", null);
        User existingUser = new User(
                "google-456", "다른 사용자", "사용중", "other@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByNickname("사용중")).thenReturn(Optional.of(existingUser));

        jwt = serviceJwt("1");

        assertThatThrownBy(() -> userService.updateMe(jwt, new UserDto.UpdateRequest("사용중")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void withdrawDeletesTheAuthenticatedUser() {
        User user = new User("google-123", "홍길동", "노브작가", "user@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        jwt = serviceJwt("1");

        userService.withdraw(jwt);

        verify(userRepository).delete(user);
    }

    @Test
    void userRemovalCascadesToOwnedBuilds() throws NoSuchFieldException {
        OneToMany relationship = User.class.getDeclaredField("myBuilds").getAnnotation(OneToMany.class);

        assertThat(relationship.mappedBy()).isEqualTo("user");
        assertThat(relationship.cascade()).contains(CascadeType.REMOVE);
    }

    @Test
    void adminCanGetAllUsers() {
        User user = new User("google-123", "홍길동", "노브작가", "user@example.com", null);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto.Response> users = userService.getAllUsers(serviceJwt("1", "admin@example.com"));

        assertThat(users).hasSize(1);
        assertThat(users.getFirst().userNickname()).isEqualTo("노브작가");
    }

    @Test
    void nonAdminCannotGetAllUsers() {
        assertThatThrownBy(() -> userService.getAllUsers(serviceJwt("1", "user@example.com")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    private Jwt serviceJwt(String subject) {
        return serviceJwt(subject, "user@example.com");
    }

    private Jwt serviceJwt(String subject, String email) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(300),
                Map.of("alg", "HS256"), Map.of("sub", subject, "email", email));
    }
}
