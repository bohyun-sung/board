package com.toyproject.board.api.config.properties;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProperty {

    private final Key key;
    private final long tokenValidityInMilliseconds;

    public JwtTokenProperty(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.token-validity}") Long tokenValidity) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenValidityInMilliseconds = tokenValidity;
    }

    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 서명이거나 토큰 형식이 잘못된 경우
            return false;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            return false;
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 토큰인 경우
            return false;
        } catch (IllegalArgumentException e) {
            // 토큰이 비어있거나 잘못된 경우
            return false;
        }
    }
}
