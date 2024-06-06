package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class InvalidDomainException extends BadRequestException{

    public InvalidDomainException(final ErrorCode exceptionCode) {
        super(exceptionCode);
    }
}
