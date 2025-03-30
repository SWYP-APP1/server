package com.swyp.futsal.provider;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    private SecretKey key;

    private SecretKey getKey() {
        if (key == null) {
            key = Jwts.SIG.HS256.key().build();
        }
        return key;
    }

    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidityInSeconds);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidityInSeconds);
    }

    private String createToken(String userId, long validityInSeconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
                .claim("sub", userId)
                .issuedAt(now)
                .expiration(validity)
                .signWith(getKey())
                .compact();
    }

    public String getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("sub", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }
}