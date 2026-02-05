package com.toyproject.board.api.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 로그인 REQ")
public record AdminLoginReq(
        @Schema(description = "관리자 ID", example = "admin_5")
        String userId,
        @Schema(description = "관리자 비밀번호", example = "123!")
        String password
) {

}
