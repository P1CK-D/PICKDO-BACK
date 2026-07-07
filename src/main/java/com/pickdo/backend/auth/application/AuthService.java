package com.pickdo.backend.auth.application;

import com.pickdo.backend.auth.domain.RefreshToken;
import com.pickdo.backend.auth.infrastructure.RefreshTokenRepository;
import com.pickdo.backend.global.security.JwtUtil;
import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String tokenValue = jwtUtil.generateRefreshToken(user.getEmail());
        RefreshToken refreshToken = new RefreshToken(
                tokenValue,
                user,
                LocalDateTime.now().plusDays(7)
        );
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenRefreshResult refreshAccessToken(String refreshTokenValue) {
        if (!jwtUtil.isValid(refreshTokenValue) || !jwtUtil.isRefreshToken(refreshTokenValue)) {
            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 RefreshToken입니다."));

        if (stored.isRevoked()) {
            throw new IllegalArgumentException("이미 폐기된 RefreshToken입니다.");
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 RefreshToken입니다.");
        }

        String email = jwtUtil.getEmail(refreshTokenValue);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        stored.revoke();

        String newAccessToken = jwtUtil.generateAccessToken(email);
        RefreshToken newRefreshToken = createRefreshToken(user);

        return new TokenRefreshResult(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void revokeRefreshToken(String refreshTokenValue) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 RefreshToken입니다."));
        stored.revoke();
    }

    public record TokenRefreshResult(String accessToken, String refreshToken) {
    }
}
