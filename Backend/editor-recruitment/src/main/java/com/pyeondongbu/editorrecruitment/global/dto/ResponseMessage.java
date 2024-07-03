package com.pyeondongbu.editorrecruitment.global.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseMessage {

    FIRST_LOGIN_OR_ENTER_DETAILS_CHECK(1001, "최초 로그인 또는 세부정보가 없습니다."),
    EXIST_LOGIN_CHECK(1002, "기존 회원의 로그인 요청입니다."),

    SUCCESS_REQUEST(2001, "정상적인 요청입니다");



    private final int status;
    private final String message;
}
