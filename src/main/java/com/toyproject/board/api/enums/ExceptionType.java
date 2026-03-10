package com.toyproject.board.api.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    // 400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "bad.request"),
    BAD_REQUEST_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "password.mismatch"),
    BAD_REQUEST_EMPTY_FILE(HttpStatus.BAD_REQUEST, "empty.file"),
    BAD_REQUEST_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "file.size.exceeded"),
    BAD_REQUEST_INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "invalid.parent.comment"),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "unauthorized"),
    UNAUTHORIZED_LOGIN_FAIL_ADMIN(HttpStatus.UNAUTHORIZED, "Login.fail.admin"),
    UNAUTHORIZED_LOGIN_FAIL_MEMBER(HttpStatus.UNAUTHORIZED, "Login.fail.member"),
    UNAUTHORIZED_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "token.invalid"),
    UNAUTHORIZED_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "token.notFound"),
    UNAUTHORIZED_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "token.expired"),
    UNAUTHORIZED_TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "token.unsupported"), // 지원되지 않음
    UNAUTHORIZED_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "token.empty"),
    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "forbidden"),
    FORBIDDEN_UPLOAD_TIME_OUT(HttpStatus.FORBIDDEN, "forbidden.upload.time.out"),
    // 404 NOT_FOUND
    NOT_FOUND(HttpStatus.NOT_FOUND, "not.found"),
    NOT_FOUND_ADMIN(HttpStatus.NOT_FOUND, "not.found.admin"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "not.found.member"),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, "not.found.email"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "not.found.post"),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "not.found.comment"),
    // 409 CONFLICT
    CONFLICT(HttpStatus.CONFLICT, "conflict"),
    CONFLICT_CREATE_DUPLICATE_ID(HttpStatus.CONFLICT, "duplicate.id"),
    CONFLICT_CREATE_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "duplicate.email"),
    // 410 GONE
    GONE_ALREADY_DELETED_COMMENT(HttpStatus.GONE, "gone.already.deleted.comment"),
    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal.server.error"),
    INTERNAL_SERVER_ERROR_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "file.upload"),
    ;
    private final HttpStatus httpStatus;
    private final String messageKey;

    ExceptionType(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
}
