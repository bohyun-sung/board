package com.toyproject.board.api.dto.admin;

import com.toyproject.board.api.dto.auth.TokenDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminLoginDto {

    private final Long adminIdx;

    private final String userId;

    private final TokenDto tokenDto;

    public static AdminLoginDto from(Long adminIdx, String userId, TokenDto tokenDto) {
        return new AdminLoginDto(
                adminIdx,
                userId,
                tokenDto
        );
    }
}
