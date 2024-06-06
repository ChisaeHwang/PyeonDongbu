package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class TagException extends RuntimeException{

    private final int status;
    private final String message;

    public TagException(final ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

}
