package com.toyproject.board.api.config.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

@Getter
public class ClientException extends CommonException{

    public ClientException(ExceptionType type) {
        super(type);
    }

    public ClientException(ExceptionType type, Object[] args) {
        super(type, args);
    }

    public ClientException(ExceptionType type, String message) {
        super(type, message);
    }

    public ClientException(String message, ExceptionType type) {
        super(message, type);
    }

    public ClientException(String message, ExceptionType type, Object[] args) {
        super(message, type, args);
    }

    public ClientException(String message, Throwable cause, ExceptionType type) {
        super(message, cause, type);
    }

    public ClientException(String message, Throwable cause, ExceptionType type, Object[] args) {
        super(message, cause, type, args);
    }
}
