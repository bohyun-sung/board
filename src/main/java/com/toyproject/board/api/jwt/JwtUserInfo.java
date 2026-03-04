package com.toyproject.board.api.jwt;

import com.toyproject.board.api.enums.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUserInfo {
    private Long userIdx;
    private RoleType roleType;

    public static JwtUserInfo from(Long userIdx, RoleType roleType) {
        return new JwtUserInfo(userIdx, roleType);
    }
}
