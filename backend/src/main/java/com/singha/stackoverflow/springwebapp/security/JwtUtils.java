package com.singha.stackoverflow.springwebapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Generate a secure signing key for HS256 encryption
    private static final String SECRET_STRING = "your-super-secret-secure-key-that-is-at-least-thirty-two-bytes-long";
    private final SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET_STRING.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    // 1. Generate token when user logs in successfully
    public String generateToken(String username) {
        // 24 hours in milliseconds
        long EXPIRATION_TIME = 86400000;
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 2. Extract username out of an incoming token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 3. Verify if token is expired or altered
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}