package com.toyproject.board.api.dto.auth.response;

import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.auth.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 로그인 RES")
public record AdminLoginRes(

        @Schema(description = "관리자", example = "1")
        Long adminIdx,

        @Schema(description = "관리자", example = "admin_5")
        String userId,
        @Schema(description = "JWT 토큰 객체")
        TokenDto token
) {
    public static AdminLoginRes from(AdminLoginDto dto) {
        return new AdminLoginRes(
                dto.getAdminIdx(),
                dto.getUserId(),
                dto.getTokenDto()
        );
    }
}
