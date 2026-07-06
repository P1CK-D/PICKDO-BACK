package com.pickdo.backend.user.application;

import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.domain.UserProfile;
import com.pickdo.backend.user.infrastructure.UserProfileRepository;
import com.pickdo.backend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public Map<String, Object> getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자 프로필을 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("email", user.getEmail());
        result.put("role", user.getRole().name());
        result.put("nickname", profile.getNickname());
        result.put("level", profile.getLevel());
        result.put("currentExp", profile.getCurrentExp());
        result.put("createdAt", user.getCreatedAt());
        return result;
    }
}
