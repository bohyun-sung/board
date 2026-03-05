package com.toyproject.board.api.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     *  [커스텀 에러 처리]
     */
    @ExceptionHandler({ClientException.class, ServerException.class})
    public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException e) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = e.getMessage();
        if (e instanceof ClientException ce) {
            status = ce.getType().getHttpStatus();
            log.info("ClientException: status={}, message={}", ce.getType().getHttpStatus(), e.getMessage());
        } else if (e instanceof ServerException se) {
            log.error("ServerException: status={}, message={}", se.getType().getHttpStatus(), e.getMessage());
        }

        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));

    }

    /**
     *  [Bean Validation 예외 처리]
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String field = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getField() : "unknown";
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.warn("Validation failed for field [{}]: {}", field, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    /**
     * [Security 예외 처리]
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {

        log.warn("Access Denied: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                        "접근 권한이 없습니다."));
    }

    /**
     * [최상위 예외 처리]
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Unhandled Exception: ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "서버 내부 오류가 발생했습니다 관리자에게 문의하세요"));
    }
}
