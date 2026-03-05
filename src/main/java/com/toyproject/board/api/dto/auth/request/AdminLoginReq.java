package com.toyproject.board.api.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "관리자 로그인 REQ")
public record AdminLoginReq(
        @Schema(description = "관리자 ID", example = "admin_5")
        @NotBlank(message = "{NotBlank.userId}")
        String userId,
        @Schema(description = "관리자 비밀번호", example = "123!")
        @NotBlank(message = "{NotBlank.password}")
        String password
) {

}
