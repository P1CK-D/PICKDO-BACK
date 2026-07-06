package com.pickdo.backend.global.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("AccessToken 생성 및 검증")
    void generateAndValidateAccessToken() {
        String email = "test@example.com";

        String accessToken = jwtUtil.generateAccessToken(email);

        assertThat(jwtUtil.isValid(accessToken)).isTrue();
        assertThat(jwtUtil.isAccessToken(accessToken)).isTrue();
        assertThat(jwtUtil.isRefreshToken(accessToken)).isFalse();
        assertThat(jwtUtil.isExpired(accessToken)).isFalse();
        assertThat(jwtUtil.getEmail(accessToken)).isEqualTo(email);
    }

    @Test
    @DisplayName("RefreshToken 생성 및 검증")
    void generateAndValidateRefreshToken() {
        String email = "test@example.com";

        String refreshToken = jwtUtil.generateRefreshToken(email);

        assertThat(jwtUtil.isValid(refreshToken)).isTrue();
        assertThat(jwtUtil.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtUtil.isAccessToken(refreshToken)).isFalse();
        assertThat(jwtUtil.isExpired(refreshToken)).isFalse();
        assertThat(jwtUtil.getEmail(refreshToken)).isEqualTo(email);
    }

    @Test
    @DisplayName("만료된 토큰은 isExpired가 true")
    void expiredToken() {
        String token = jwtUtil.generateAccessToken("test@example.com");
        assertThat(jwtUtil.isExpired(token)).isFalse();
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 isValid가 false")
    void invalidToken() {
        assertThat(jwtUtil.isValid("invalid.jwt.token")).isFalse();
        assertThat(jwtUtil.isAccessToken("invalid.jwt.token")).isFalse();
    }
}
