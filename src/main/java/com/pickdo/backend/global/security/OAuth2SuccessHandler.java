package com.pickdo.backend.global.security;
import com.pickdo.backend.global.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Value("${app.frontend-url:exp://10.129.57.186:8081/--/auth/callback}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String token = jwtUtil.generateToken(email);

        String targetUrl = resolveTargetUrl(request);

        log.info("=== OAuth2 로그인 성공 ===");
        log.info("email: {}", email);
        log.info("token: {}", token);
        log.info("targetUrl: {}", targetUrl);

        clearAuthenticationAttributes(request, response);

        String separator = targetUrl.contains("?") ? "&" : "?";
        String finalUrl = targetUrl + separator + "token=" + token;

        log.info("finalUrl (redirect): {}", finalUrl);

        response.sendRedirect(finalUrl);
    }

    private String resolveTargetUrl(HttpServletRequest request) {
        Optional<Cookie> cookie = CookieUtils.getCookie(
                request,
                HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME
        );

        if (cookie.isPresent()) {
            log.info("쿠키에서 redirect_uri 찾음: {}", cookie.get().getValue());
            return cookie.get().getValue();
        } else {
            log.warn("쿠키에 redirect_uri 없음 → 기본 frontendUrl 사용: {}", frontendUrl);
            return frontendUrl;
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}