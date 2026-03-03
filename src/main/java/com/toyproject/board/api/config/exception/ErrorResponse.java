package com.toyproject.board.api.config.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
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

    @Getter
    @NoArgsConstructor
    public static class ErrorSubResponse {

        private Integer code;

        private String message;

        public ErrorSubResponse(Integer code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
