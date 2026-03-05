package com.toyproject.board.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 DTO")
public record TokenDto(
        @Schema(description = "엑세스 토큰")
        String accessToken,
        @Schema(description = "리프레쉬 토큰")
        String refreshToken
) {
    public static TokenDto from(String accessToken, String refreshToken) {
        return new TokenDto(accessToken, refreshToken);
    }
}
