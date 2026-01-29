package com.toyproject.board.api.dto.admin.response;

import com.toyproject.board.api.dto.admin.AdminLoginDto;

public record AdminLoginRes(
        Long idx,
        String userId,
        String token
) {
    public static AdminLoginRes from(AdminLoginDto dto) {
        return new AdminLoginRes(
                dto.getIdx(),
                dto.getUserId(),
                dto.getToken()
        );
    }
}
