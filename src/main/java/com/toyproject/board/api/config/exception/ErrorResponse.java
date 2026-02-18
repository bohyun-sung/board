package com.toyproject.board.api.config.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final boolean result = false;

    private ErrorSubResponse fail;

    public ErrorResponse(Integer code, String message) {
        this.fail = new ErrorSubResponse(code, message);
    }
}
