package com.toyproject.board.api.dto.admin.response;

import com.toyproject.board.api.dto.admin.AdminDto;

import java.time.LocalDateTime;

public record AdminSearchAdminRes (
        Long idx,
        String name,
        String userId,
        String email,
        String phone,
        LocalDateTime rgdt,
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
