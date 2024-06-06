package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final int status;
    private final String message;

    public AuthException(final ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
