package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class SearchException extends BadRequestException{

    public SearchException(final ErrorCode errorCode) {
        super(errorCode);
    }

}
