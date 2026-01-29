package com.toyproject.board.api.dto.admin.request;

import com.toyproject.board.api.domain.admin.entity.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "관리자 생성 DTO")
public record AdminCreateReq(
        @Schema(description = "관리자 성함", example = "홍길동")
        @NotBlank(message = "{NotBlank.name}")
        String name,

        @Schema(description = "로그인용 아이디", example = "admin_1")
        @NotBlank(message = "{NotBlank.userId}")
        String userId,

        @Schema(description = "로그인용 비밀번호", example = "123!")
        @NotBlank(message = "{NotBlank.password}")
        String password,

        @Schema(description = "연락 가능한 이메일", example = "admin@example.com")
        @NotBlank(message = "{NotBlank.email}")
        String email,

        @Schema(description = "핸드폰 번호", example = "010-0000-0000")
        @NotBlank(message = "{NotBlank.phone}")
        String phone
) {
        public Admin toEntity() {
                return Admin.of(
                        this.name,
                        this.userId,
                        this.password,
                        this.email,
                        this.phone,
                        "ADMIN"
                );
        }
}
