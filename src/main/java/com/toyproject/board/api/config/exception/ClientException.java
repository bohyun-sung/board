package com.toyproject.board.api.config.exception;

import com.toyproject.board.api.enums.ExceptionType;
import lombok.Getter;

/**
 * [사용법]
 * 1. throw new ClientException(ExceptionType.CONFLICT_CREATE_DUPLICATE_ID);
 * 출력: "이미 사용 중인 아이디입니다."
 * 2. throw new ClientException(ExceptionType.NOT_FOUND_USER, new Object[]{ userId });
 * 출력: "해당 아이디 [gemini123]를 찾을 수 없습니다." (변역 + 값 치환)
 * 3. throw new ClientException(ExceptionType.BAD_REQUEST, "이 메시지는 예외적으로 직접 입력합니다.");
 * 출력: "이 메시지는 예외적으로 직접 입력합니다." (번역 시도 후 실패하여 원문 노출)
 */
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

    public ClientException(ExceptionType type, Throwable cause) {
        super(type, cause);
    }
}
