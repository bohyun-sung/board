package com.toyproject.board.api.dto.member;

import com.toyproject.board.api.dto.auth.TokenDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberLoginDto {
    private final Long memberIdx;
    private final TokenDto tokenDto;

    public static MemberLoginDto from(Long memberIdx, TokenDto tokenDto) {
        return new MemberLoginDto(memberIdx, tokenDto);
    }
}
