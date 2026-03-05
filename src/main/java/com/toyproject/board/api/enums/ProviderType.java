package com.toyproject.board.api.enums;

import com.toyproject.board.api.enums.coverter.BaseEnumAttributeConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType implements BaseEnum {

    LOCAL(0, "local"),
    GOOGLE(1, "google"),
    ;
    private final Integer value;
    private final String text;

    @jakarta.persistence.Converter
    public static class Converter extends BaseEnumAttributeConverter<ProviderType> {
        public Converter() {super(ProviderType.class);}
    }
}
