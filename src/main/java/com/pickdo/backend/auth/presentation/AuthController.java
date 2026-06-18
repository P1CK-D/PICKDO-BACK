package com.pickdo.backend.auth.presentation;

import com.pickdo.backend.global.response.ResponseEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/logout")
    public ResponseEnvelope<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEnvelope.success(Map.of("message", "로그아웃 성공"));
    }
}
