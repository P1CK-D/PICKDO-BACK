package com.pickdo.backend.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String TOKEN_TYPE = "type";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;        // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(String email, String type, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .claim(TOKEN_TYPE, type)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isAccessToken(String token) {
        try {
            return ACCESS_TOKEN.equals(extractClaims(token).get(TOKEN_TYPE));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return REFRESH_TOKEN.equals(extractClaims(token).get(TOKEN_TYPE));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            extractClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
