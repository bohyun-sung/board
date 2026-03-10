package com.toyproject.board.api.dto.member;

import com.toyproject.board.api.enums.ProviderType;
import com.toyproject.board.api.enums.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDto {
    private final Long memberIdx;
    private final String email;
    private final String nickname;
    private final String phone;
    private final String password;
    private final RoleType roleType;
    private final ProviderType providerType;
}
