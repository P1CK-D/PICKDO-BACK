package com.pickdo.backend.global.security;

import com.pickdo.backend.user.infrastructure.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            if (!jwtUtil.isAccessToken(token)) {
                sendError(response, "INVALID_TOKEN_TYPE", "AccessToken이 아닙니다.");
                return;
            }

            String email = jwtUtil.getEmail(token);

            UserDetails userDetails = userRepository.findByEmail(email)
                    .filter(user -> !user.isDeleted())
                    .map(user -> org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password("")
                            .authorities(user.getRole().name())
                            .build())
                    .orElse(null);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            sendError(response, "EXPIRED_TOKEN", "AccessToken이 만료되었습니다.");
            return;
        } catch (Exception e) {
            sendError(response, "INVALID_TOKEN", "유효하지 않은 AccessToken입니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"success\":false,\"error\":{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}}"
        );
    }
}
