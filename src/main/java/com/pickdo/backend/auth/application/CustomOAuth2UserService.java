package com.pickdo.backend.auth.application;

import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.domain.UserProfile;
import com.pickdo.backend.user.infrastructure.UserProfileRepository;
import com.pickdo.backend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");

        try {
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(new User(email)));

            userProfileRepository.findByUser(user)
                    .ifPresentOrElse(
                            profile -> {
                                profile.updateNickname(nickname);
                                userProfileRepository.save(profile);
                            },
                            () -> userProfileRepository.save(new UserProfile(user, nickname))
                    );

        } catch (Exception e) {
            log.error("Failed to save/update user during OAuth2 login: {}", e.getMessage(), e);
            throw e;
        }

        return oAuth2User;
    }
}
