package com.toyproject.board.api.enums;

import java.util.EnumSet;

public interface BaseEnum {

    Integer getValue();

    String getText();

    static <E extends Enum<E> & BaseEnum> E getByValue(Class<E> enumClass, Integer value) {
        if (value == null) return null;
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    static <E extends Enum<E> & BaseEnum> E getByText(Class<E> enumClass, String text) {
        if (text == null || text.isBlank()) return null;
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}

