package com.toyproject.board.api.config.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {

    private final ExceptionType type;
    private final Object[] args;


    public CommonException(ExceptionType type) {
        this.type = type;
        this.args = null;
    }

    public CommonException(ExceptionType type, String message) {
        super(message);
        this.type = type;
        this.args = null;
    }

    public CommonException(ExceptionType type, Object[] args) {
        this.type = type;
        this.args = args;
    }

    public CommonException(String message, ExceptionType type) {
        super(message);
        this.type = type;
        this.args = null;
    }

    public CommonException(String message, ExceptionType type, Object[] args) {
        super(message);
        this.type = type;
        this.args = args;
    }

    public CommonException(String message, Throwable cause, ExceptionType type) {
        super(message, cause);
        this.type = type;
        this.args = null;
    }

    public CommonException(String message, Throwable cause, ExceptionType type, Object[] args) {
        super(message, cause);
        this.type = type;
        this.args = args;
    }

}
