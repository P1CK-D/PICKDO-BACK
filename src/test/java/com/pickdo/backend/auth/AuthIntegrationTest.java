package com.pickdo.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickdo.backend.auth.application.AuthService;
import com.pickdo.backend.auth.domain.RefreshToken;
import com.pickdo.backend.auth.infrastructure.RefreshTokenRepository;
import com.pickdo.backend.global.security.JwtUtil;
import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.domain.UserProfile;
import com.pickdo.backend.user.infrastructure.UserProfileRepository;
import com.pickdo.backend.user.infrastructure.UserRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private User testUser;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
        testUser = userRepository.save(new User("test@example.com"));
        userProfileRepository.save(new UserProfile(testUser, "tester"));
    }

    @Test
    @DisplayName("유효한 RefreshToken으로 새 AccessToken 발급")
    void refreshAccessToken() throws Exception {
        RefreshToken saved = authService.createRefreshToken(testUser);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", saved.getToken()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andExpect(jsonPath("$.data.refreshToken").isString());

        assertThat(refreshTokenRepository.findById(saved.getId()))
                .map(RefreshToken::isRevoked)
                .hasValue(true);
    }

    @Test
    @DisplayName("만료된 RefreshToken으로 요청 시 401")
    void expiredRefreshToken() throws Exception {
        String email = testUser.getEmail();
        String tokenValue = jwtUtil.generateRefreshToken(email);
        refreshTokenRepository.save(new RefreshToken(tokenValue, testUser, java.time.LocalDateTime.now().minusDays(1)));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tokenValue))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("이미 폐기된 RefreshToken으로 요청 시 401")
    void revokedRefreshToken() throws Exception {
        String email = testUser.getEmail();
        String tokenValue = jwtUtil.generateRefreshToken(email);
        refreshTokenRepository.save(new RefreshToken(tokenValue, testUser, java.time.LocalDateTime.now().plusDays(7)));
        transactionTemplate.execute(status -> {
            RefreshToken managed = refreshTokenRepository.findByToken(tokenValue).orElseThrow();
            managed.revoke();
            refreshTokenRepository.flush();
            return null;
        });

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", tokenValue))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("AccessToken으로 refresh 요청 시 401")
    void accessTokenCannotRefresh() throws Exception {
        String accessToken = jwtUtil.generateAccessToken(testUser.getEmail());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", accessToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
