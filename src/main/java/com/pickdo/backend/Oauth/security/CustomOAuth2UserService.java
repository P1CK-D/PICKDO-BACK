package com.pickdo.backend.Oauth.security;

import com.pickdo.backend.Oauth.entity.User;
import com.pickdo.backend.Oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");

        // 유저 없으면 자동 회원가입
        userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email, nickname)));

        return oAuth2User;
    }
}