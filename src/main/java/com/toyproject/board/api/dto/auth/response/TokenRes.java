package com.toyproject.board.api.dto.auth.response;

public record TokenRes(
        String accessToken,
        String refreshToken
) {
    public static TokenRes from(String accessToken, String refreshToken) {
        return new TokenRes(accessToken, refreshToken);
    }
}
