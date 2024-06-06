package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;

@Getter
public class ImageException extends BadRequestException {

    public ImageException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
