package com.toyproject.board.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_MEMBER = "hasRole('USER')";
    public static final long TTL_ZERO = 0L;

    // 런타임시 환경변수 값을 가져오지못해 정의 했음 [RefreshToken.class]
    public static final long REFRESH_TOKEN_MAX_AGE = 14 * 24 * 60 * 60L;
}
