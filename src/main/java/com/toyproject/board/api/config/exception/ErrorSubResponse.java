package com.toyproject.board.api.config.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorSubResponse {

    private final Integer code;

    private final String message;


}
