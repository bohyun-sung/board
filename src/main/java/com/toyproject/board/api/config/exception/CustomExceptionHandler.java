package com.toyproject.board.api.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(
            HttpServletRequest request,
            ClientException e) {

        log.error("ClientException: ", e);

        return ResponseEntity.status(e.getType().getHttpStatus())
                .body(
                        new ErrorResponse(
                                e.getType().getHttpStatus().value(),
                                e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            HttpServletRequest request,
            Exception e) {

        log.error("Exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                e.getMessage()));
    }
}
