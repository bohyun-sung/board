package com.toyproject.board.api.config.properties;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.jwt.JwtUserInfo;
import com.toyproject.board.api.jwt.properties.AppJwtProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProperty {

    private final AppJwtProperties appJwtProperties;
    private static final String ROLE_KEY = "role";

    /**
     * accessToken create
     */
    public String createToken(Long idx, RoleType roleType) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(idx));
        claims.put(ROLE_KEY, roleType.name());

        Date now = new Date();
        Date validity = new Date(now.getTime() + appJwtProperties.getAccessTTL());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(appJwtProperties.getSecretAsObject(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * RefreshToken create
     */
    public String createRefreshToken(Long idx, RoleType roleType) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(idx));
        claims.put(ROLE_KEY, roleType.name());
        Date now = new Date();
        Date validity = new Date(now.getTime() + appJwtProperties.getRefreshTTL()); // 14일

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(appJwtProperties.getSecretAsObject(), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUserInfo getUserInfo(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(appJwtProperties.getSecretAsObject())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userIdx = Long.parseLong(claims.getSubject());
        RoleType roleType = RoleType.valueOf(claims.get(ROLE_KEY, String.class));

        return JwtUserInfo.from(userIdx, roleType);
    }

    /**
     * jwt 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(appJwtProperties.getSecretAsObject()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 서명이거나 토큰 형식이 잘못된 경우: {}", e.getMessage());
            throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_INVALID);
        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료된 경우: {}", e.getMessage());
            throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 토큰인 경우: {}", e.getMessage());
            throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            log.error("토큰이 비어있거나 잘못된 경우: {}", e.getMessage());
            throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_EMPTY);
        }
    }

    /**
     * 엑세스 토큰의 남은시간 반환
     * @param accessToken accessToken
     * @return 토큰의 남은시간
     */
    public long getExpiration(String accessToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(appJwtProperties.getSecretAsObject())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        return claims.getExpiration().getTime();
    }

    /**
     * Bearer 토큰에서 실제 토큰 값만 추출
     */
    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConstants.BEARER_PREFIX)) {
            return bearerToken.substring(AuthConstants.BEARER_PREFIX.length());
        }
        return null;
    }
}
