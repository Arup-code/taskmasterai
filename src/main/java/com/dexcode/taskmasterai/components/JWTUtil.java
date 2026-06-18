package com.dexcode.taskmasterai.components;

import com.dexcode.taskmasterai.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry}")
    private Long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry}")
    private Long refreshTokenExpiry;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user, String refreshToken) {
        Claims claims = Jwts.claims().build();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("refresh_token", refreshToken);
        claims.put("role", "USER");

        return Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public Claims getClaimsFromToken(String token, boolean isExpired) throws JwtException {
        try {
            if(isExpired) {
                return Jwts.parser()
                  .decryptWith(getSigningKey())
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
            }
            return Jwts.parser()
                  .verifyWith(getSigningKey())
                  .build()
                  .parseSignedClaims(token)
                  .getPayload();
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token");
        }
    }

    public boolean isAccessTokenValid(String token) throws JwtException {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            throw new JwtException("Invalid token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
