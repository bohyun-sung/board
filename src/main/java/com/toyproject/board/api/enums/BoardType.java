package com.toyproject.board.api.enums;

import com.toyproject.board.api.enums.coverter.BaseEnumAttributeConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType implements BaseEnum {

    NOTICE(0, "공지"),
    EVENT(1, "이벤트"),
    NEWS(2, "뉴스"),
    FREE(3, "자유게시판")
    ;

    private final Integer value;

    private final String text;

    @jakarta.persistence.Converter
    public static class Converter extends BaseEnumAttributeConverter<BoardType> {

        public Converter() {
            super(BoardType.class);
        }
    }
}
