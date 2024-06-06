package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class TokenInvalidException extends RuntimeException{

    private final int status;
    private final String message;

    public TokenInvalidException(final ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

}
