package com.toyproject.board.api.dto.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminLoginDto {

    private final Long idx;
    private final String userId;
    private final String token;

    public static AdminLoginDto from(Long idx, String userId, String token) {
        return new AdminLoginDto(
                idx,
                userId,
                token
        );
    }
}
