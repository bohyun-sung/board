package com.toyproject.board.api.dto.admin;

import com.toyproject.board.api.domain.admin.entity.Admin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminDto {

    private final Long idx;
    private final String name;
    private final String userId;
    private final String password;
    private final String email;
    private final String phone;
    private final LocalDateTime rgdt;
    private final LocalDateTime updt;

    public static AdminDto from(Admin entity) {
        return new AdminDto(
                entity.getIdx(),
                entity.getName(),
                entity.getUserId(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRgdt(),
                entity.getUpdt()
        );
    }
}
