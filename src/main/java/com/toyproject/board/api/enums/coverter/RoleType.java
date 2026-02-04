package com.toyproject.board.api.enums.coverter;

import com.toyproject.board.api.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType implements BaseEnum {

    ADMIN(0, "관리자"),
    USER(1, "이용자"),
    ;

    private final Integer value;
    private final String text;

    @jakarta.persistence.Converter
    public static class Converter extends BaseEnumAttributeConverter<RoleType> {
        public Converter() {
            super(RoleType.class);
        }
    }

    public String getKey() {
        return "ROLE_" + this.name();
    }
}
