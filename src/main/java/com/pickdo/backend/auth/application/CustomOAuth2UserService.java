package com.pickdo.backend.auth.application;

import com.pickdo.backend.user.domain.User;
import com.pickdo.backend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");

        userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(email)
                        .map(user -> {
                            user.linkGoogleId(googleId);
                            user.updateNickname(nickname);
                            return user;
                        }))
                .orElseGet(() -> userRepository.save(new User(email, googleId, nickname)));

        return oAuth2User;
    }
}
