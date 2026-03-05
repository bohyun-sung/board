package com.toyproject.board.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstants {
    public static final String LOGOUT_ACCESS_TOKEN = "logout:AT:";
    public static final String LOGOUT = "logout";
    public static final String VIEW_COUNT_KEY = "post:view:count:";
    public static final String USER_VIEW_CHECK_KEY = "post:view:user:";
    public static final String UPLOAD_OWNER_KEY = "upload_owner:";
    public static final Duration UPLOAD_OWNER_TTL = Duration.ofMinutes(3);

}
