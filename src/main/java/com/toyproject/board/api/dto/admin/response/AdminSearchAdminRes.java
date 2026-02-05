package com.toyproject.board.api.dto.admin.response;

import com.toyproject.board.api.dto.admin.AdminDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "관리자 목록 검색 RES")
public record AdminSearchAdminRes (

        @Schema(description = "관리자 식별번호")
        Long adminIdx,

        @Schema(description = "관리자 이름")
        String name,

        @Schema(description = "관리자 아이디")
        String userId,

        @Schema(description = "관리자 이메일")
        String email,

        @Schema(description = "관리자 전화번호")
        String phone,

        @Schema(description = "관리자 등록시간")
        LocalDateTime rgdt,

        @Schema(description = "관리자 수정시간")
        LocalDateTime updt
){

    public static AdminSearchAdminRes from(AdminDto dto) {
        return new AdminSearchAdminRes(
                dto.getIdx(),
                dto.getName(),
                dto.getUserId(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getRgdt(),
                dto.getUpdt()
        );
    }
}
