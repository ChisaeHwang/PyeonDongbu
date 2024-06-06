package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends AuthException {

    public RefreshTokenException(final ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
