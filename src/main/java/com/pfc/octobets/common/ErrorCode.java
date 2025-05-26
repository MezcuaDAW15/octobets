package com.pfc.octobets.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    BET_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    BET_ALREADY_PLACED(HttpStatus.CONFLICT),
    STAKE_LIMIT_REACHED(HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
