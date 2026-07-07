package com.pickdo.backend.auth.presentation;

import com.pickdo.backend.auth.application.AuthService;
import com.pickdo.backend.global.response.ResponseEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEnvelope<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
        authService.revokeRefreshToken(request.refreshToken());
        SecurityContextHolder.clearContext();
        return ResponseEnvelope.success(Map.of("message", "로그아웃 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseEnvelope<Map<String, String>>> refresh(
            @RequestBody RefreshTokenRequest request) {
        try {
            AuthService.TokenRefreshResult result = authService.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(ResponseEnvelope.success(Map.of(
                    "accessToken", result.accessToken(),
                    "refreshToken", result.refreshToken()
            )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(ResponseEnvelope.error("INVALID_REFRESH_TOKEN", e.getMessage(), null));
        }
    }

    private record RefreshTokenRequest(String refreshToken) {
    }
}
