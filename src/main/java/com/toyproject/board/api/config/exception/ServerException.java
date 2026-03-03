package com.toyproject.board.api.config.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

@Getter
public class ServerException extends CommonException{

    public ServerException(ExceptionType type) {
        super(type);
    }
    public ServerException(ExceptionType type, Object[] args) {
        super(type, args);
    }

    public ServerException(ExceptionType type, String message) {
        super(type, message);
    }

    public ServerException(String message, ExceptionType type) {
        super(message, type);
    }

    public ServerException(String message, ExceptionType type, Object[] args) {
        super(message, type, args);
    }

    public ServerException(String message, Throwable cause, ExceptionType type) {
        super(message, cause, type);
    }

    public ServerException(String message, Throwable cause, ExceptionType type, Object[] args) {
        super(message, cause, type, args);
    }
}
