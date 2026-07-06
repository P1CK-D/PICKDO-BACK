package com.pickdo.backend.user.presentation;

import com.pickdo.backend.global.response.ResponseEnvelope;
import com.pickdo.backend.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEnvelope<Map<String, Object>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEnvelope.success(userService.getMyProfile(userDetails.getUsername()));
    }
}
