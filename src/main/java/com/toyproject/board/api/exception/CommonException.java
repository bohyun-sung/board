package com.toyproject.board.api.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {

    private final ExceptionType type;
    private final Object[] args;


    // 1. 기본 생성자 (Type만 전달)
    public CommonException(ExceptionType type) {
        super(type.getMessageKey());
        this.type = type;
        this.args = null;
    }

    // 2. 동적 인자 포함 생성자 (추천: {0} 등의 파라미터가 있을 때)
    public CommonException(ExceptionType type, Object[] args) {
        super(type.getMessageKey());
        this.type = type;
        this.args = args;
    }

    // 3. 메시지를 직접 커스텀하여 덮어쓰고 싶을 때
    public CommonException(ExceptionType type, String message) {
        super(message);
        this.type = type;
        this.args = null;
    }

    // 4. 예외 체이닝용 (Cause 포함)
    public CommonException(ExceptionType type, Throwable cause) {
        super(type.getMessageKey(), cause);
        this.type = type;
        this.args = null;
    }

    // 5. 메시지 직접 입력 + 인자 포함 (특수 케이스)
    public CommonException(String message, ExceptionType type, Object[] args) {
        super(message);
        this.type = type;
        this.args = args;
    }

    // 6. 모든 정보를 포함하는 생성자
    public CommonException(String message, Throwable cause, ExceptionType type, Object[] args) {
        super(message, cause);
        this.type = type;
        this.args = args;
    }

}
