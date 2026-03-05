package com.toyproject.board.api.dto.member.request;

import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.enums.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "신규 멤버 가입 REQ")
public record MemberCreateReq(

        @Schema(description = "이메일", example = "user_1@example.com")
        @NotBlank(message = "{NotBlank.email}")
        @Email
        String email,

        @Schema(description = "닉네임[이름]", example = "user_1")
        @NotBlank(message = "{NotBlank.nickname}")
        String nickname,

        @Schema(description = "전화번호", example = "010-0000-0000")
        @NotBlank(message = "{NotBlank.phone}")
        String phone,

        @Schema(description = "비밀번호", example = "123!")
        @NotBlank(message = "{NotBlank.password}")
        String password,

        @Schema(description = "비밀번호 체크", example = "123!")
        @NotBlank(message = "{NotBlank.password}")
        String passwordCheck
) {
    public Member toEntity(String encodePassword) {
        return Member.of(
                this.email,
                this.nickname,
                this.phone,
                encodePassword,
                ProviderType.LOCAL
        );
    }
}
