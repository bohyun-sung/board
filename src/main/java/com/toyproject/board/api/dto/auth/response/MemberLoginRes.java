package com.toyproject.board.api.dto.auth.response;

import com.toyproject.board.api.dto.auth.TokenDto;
import com.toyproject.board.api.dto.member.MemberLoginDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 로그인 RES")
public record MemberLoginRes(
        @Schema(description = "멤버 idx")
        Long memberIdx,
        @Schema(description = "JWT 토큰 객체")
        TokenDto token
) {
    public static MemberLoginRes from(MemberLoginDto dto) {
        return new MemberLoginRes(
                dto.getMemberIdx(),
                dto.getTokenDto()
        );
    }
}
