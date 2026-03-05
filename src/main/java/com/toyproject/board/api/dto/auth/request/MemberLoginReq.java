package com.toyproject.board.api.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "멤버 로그인 REQ")
public record MemberLoginReq(
        @Schema(description = "이메일", example = "user_1@example.com")
        @NotBlank(message = "{NotBlank.email}")
        @Email(message = "{Email.email}") // 이메일 형식 체킹으로 잘못된 요청 차단
        String email,

        @Schema(description = "비밀번호", example = "123!")
        @NotBlank(message = "{NotBlank.password}")
        @NotBlank(message = "{NotBlank.password}")
        String password
) {
}
