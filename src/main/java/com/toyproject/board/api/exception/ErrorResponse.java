package com.toyproject.board.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "에러 코드")
        private Integer code;
        @Schema(description = "에러 메세지")
        private String message;

        public ErrorSubResponse(Integer code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
