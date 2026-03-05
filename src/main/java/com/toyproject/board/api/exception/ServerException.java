package com.toyproject.board.api.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

@Getter
public class ServerException extends CommonException {

    public ServerException(ExceptionType type) {
        super(type);
    }

    public ServerException(ExceptionType type, Object[] args) {
        super(type, args);
    }

    public ServerException(ExceptionType type, String message) {
        super(type, message);
    }

    public ServerException(ExceptionType type, Throwable cause) {
        super(type, cause);
    }
}
