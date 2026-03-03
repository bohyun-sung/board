package com.toyproject.board.api.enums;

import com.toyproject.board.api.enums.coverter.BaseEnumAttributeConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadType implements BaseEnum {

    POST(0, "게시판");

    private final Integer value;
    private final String text;

    @jakarta.persistence.Converter
    public static class Converter extends BaseEnumAttributeConverter<UploadType> {
        public Converter() {
            super(UploadType.class);
        }
    }
}
