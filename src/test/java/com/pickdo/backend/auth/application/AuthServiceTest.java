package com.pickdo.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pickdo.backend.auth.domain.RefreshToken;
import com.pickdo.backend.auth.infrastructure.RefreshTokenRepository;
import com.pickdo.backend.global.security.JwtUtil;
import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.domain.UserProfile;
import com.pickdo.backend.user.infrastructure.UserProfileRepository;
import com.pickdo.backend.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("test@example.com"));
        userProfileRepository.save(new UserProfile(testUser, "tester"));
    }

    @Test
    @DisplayName("RefreshToken 생성 후 재발급")
    void createAndRefresh() {
        RefreshToken saved = authService.createRefreshToken(testUser);

        AuthService.TokenRefreshResult result = authService.refreshAccessToken(saved.getToken());

        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotEqualTo(saved.getToken());

        assertThat(refreshTokenRepository.findById(saved.getId()))
                .map(RefreshToken::isRevoked)
                .hasValue(true);
    }

    @Test
    @Transactional
    @DisplayName("폐기된 RefreshToken으로 재발급 시 예외")
    void revokedTokenThrows() {
        RefreshToken saved = authService.createRefreshToken(testUser);
        RefreshToken managed = refreshTokenRepository.findById(saved.getId()).orElseThrow();
        managed.revoke();
        refreshTokenRepository.flush();

        assertThatThrownBy(() -> authService.refreshAccessToken(saved.getToken()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("폐기");
    }

    @Test
    @DisplayName("AccessToken으로 refresh 요청 시 예외")
    void accessTokenCannotRefresh() {
        String accessToken = jwtUtil.generateAccessToken(testUser.getEmail());

        assertThatThrownBy(() -> authService.refreshAccessToken(accessToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은");
    }

    @Test
    @DisplayName("존재하지 않는 RefreshToken으로 요청 시 예외")
    void nonExistentTokenThrows() {
        String fakeToken = jwtUtil.generateRefreshToken("nonexistent@test.com");

        assertThatThrownBy(() -> authService.refreshAccessToken(fakeToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는");
    }
}
