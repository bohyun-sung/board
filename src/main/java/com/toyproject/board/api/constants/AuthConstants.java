package com.toyproject.board.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    public static final long REFRESH_TOKEN_MAX_AGE = 14 * 24 * 60 * 60L;
}
