package com.coursivo.coursivo_backend.security;

import com.coursivo.coursivo_backend.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey signingKey;

    private final long expirationMinutes;

    public JwtUtil(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String email, UserRole role) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        parse(token);
        return true;
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public UserRole extractRole(String token) {
        String role = parse(token).get("role", String.class);
        return UserRole.valueOf(role);
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

