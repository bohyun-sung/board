package com.toyproject.board.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstants {
    public static final String LOGOUT_ACCESS_TOKEN = "logout:AT:";
    public static final String LOGOUT = "logout";
    public static final String VIEW_COUNT_KEY = "post:view:count:";
    public static final String USER_VIEW_CHECK_KEY = "post:view:user:";

}
