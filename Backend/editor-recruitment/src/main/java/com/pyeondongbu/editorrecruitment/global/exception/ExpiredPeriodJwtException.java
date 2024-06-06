package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;


@Getter
public class ExpiredPeriodJwtException extends AuthException {

    public ExpiredPeriodJwtException(final ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
