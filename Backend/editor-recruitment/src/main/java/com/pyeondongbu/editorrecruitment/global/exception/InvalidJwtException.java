package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class InvalidJwtException extends AuthException {

    public InvalidJwtException(final ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
