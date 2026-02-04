package com.toyproject.board.api.enums.coverter;

import com.toyproject.board.api.enums.BaseEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;

import java.util.Arrays;

@RequiredArgsConstructor
@Converter
public class BaseEnumAttributeConverter<T extends BaseEnum> implements AttributeConverter<T, Integer> {

    private final Class<T> clazz;

    @Override
    public Integer convertToDatabaseColumn(T t) {
        if (t == null) return null;
        return t.getValue();
    }

    @Override
    public T convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return Arrays.stream(clazz.getEnumConstants())
                    .filter(o -> o.getValue().equals(dbData))
                    .findAny()
                    .orElseThrow(() -> new BadRequestException(String.format("%S(%s) not founded", clazz.getName(), dbData)));
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
