package com.pyeondongbu.editorrecruitment.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {


    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),

    NOT_FOUND_MEMBER_ID(1011, "멤버를 찾을 수 없습니다."),
    FAIL_TO_GENERATE_RANDOM_NICKNAME(1012, "랜덤한 닉네임을 생성하는데 실패하였습니다."),
    DUPLICATED_MEMBER_NICKNAME(1013, "중복된 닉네임입니다."),
    INVALID_SOCIAL_LOGIN_ID(1014, "올바르지 않은 소셜 로그인 아이디입니다,"),
    INVALID_NICK_NAME(1015, "올바르지 않은 닉네임입니다,"),
    INVALID_MEMBER_DETAILS(1016, "올바르지 않은 멤버 세부정보 입니다"),
    INVALID_ROLE_INFO(1017, "올바르지 않은 역할입니다"),

    INVALID_TAG_NAME(2001, "존재하지 않는 태그입니다"),
    NOT_FOUND_POST_NAME(2002, "게시글을 찾을 수 없습니다"),
    INVALID_TITLE_NAME(2002, "타이틀이 없습니다"),
    INVALID_CONTENT_NULL(2003, "게시글 내용이 없습니다"),
    INVALID_SEARCH_CONTENT(2004, "검색은 최소 2자리 이상이어야 하며, 공백은 불가능합니다."),
    INVALID_PAYMENT(2005, "급여 지급 과정이 유효하지 않습니다,"),
    INVALID_POST_DETAILS(2006, "올바르지 않은 게시글 세부 정보입니다,"),


    EXCEED_IMAGE_CAPACITY(5001, "업로드 가능한 이미지 용량을 초과했습니다."),
    NULL_IMAGE(5002, "업로드한 이미지 파일이 NULL입니다."),
    EMPTY_IMAGE_LIST(5003, "최소 한 장 이상의 이미지를 업로드해야합니다."),
    EXCEED_IMAGE_LIST_SIZE(5004, "업로드 가능한 이미지 개수를 초과했습니다."),
    INVALID_IMAGE_URL(5005, "요청한 이미지 URL의 형식이 잘못되었습니다."),
    INVALID_IMAGE_PATH(5101, "이미지를 저장할 경로가 올바르지 않습니다."),
    FAIL_IMAGE_NAME_HASH(5102, "이미지 이름을 해싱하는 데 실패했습니다."),
    INVALID_IMAGE(5103, "올바르지 않은 이미지 파일입니다."),


    INVALID_SHARE_CODE(7002, "공유가 허용되지 않은 코드입니다."),
    FAIL_SHARE_CODE_HASH(7101, "공유 코드를 해싱하는 데 실패했습니다."),
    INVALID_METHOD_LOCK_KEY(7102, "유효하지 않은 메서드 이름입니다."),

    INVALID_USER_NAME(8001, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(8002, "비밀번호가 일치하지 않습니다."),
    NULL_ADMIN_AUTHORITY(8101, "잘못된 관리자 권한입니다."),
    DUPLICATED_ADMIN_USERNAME(8102, "중복된 사용자 이름입니다."),
    NOT_FOUND_ADMIN_ID(8103, "요청한 ID에 해당하는 관리자를 찾을 수 없습니다."),
    INVALID_CURRENT_PASSWORD(8104, "현재 사용중인 비밀번호가 일치하지 않습니다."),
    INVALID_IP_ADDRESS(8105, "유효하지 않은 IP 주소입니다"),
    INVALID_ADMIN_AUTHORITY(8201, "해당 관리자 기능에 대한 접근 권한이 없습니다."),
    INVALID_AUTHORIZATION_CODE(9001, "유효하지 않은 인증 코드입니다."),
    NOT_SUPPORTED_OAUTH_SERVICE(9002, "해당 OAuth 서비스는 제공하지 않습니다."),
    FAIL_TO_CONVERT_URL_PARAMETER(9003, "Url Parameter 변환 중 오류가 발생했습니다."),
    ALREADY_USED_AUTHORIZATION_CODE(9004, "이미 사용된 인증 코드입니다. 중복 로그인 시도입니다."),
    INVALID_REFRESH_TOKEN(9101, "올바르지 않은 형식의 RefreshToken입니다."),
    INVALID_ACCESS_TOKEN(9102, "올바르지 않은 형식의 AccessToken입니다."),
    EXPIRED_PERIOD_REFRESH_TOKEN(9103, "기한이 만료된 RefreshToken입니다."),
    EXPIRED_PERIOD_ACCESS_TOKEN(9104, "기한이 만료된 AccessToken입니다."),
    NOT_FOUND_ACCESS_TOKEN(9104, "헤더에 토큰이 없습니다."),

    FAIL_TO_VALIDATE_TOKEN(9105, "토큰 유효성 검사 중 오류가 발생했습니다."),
    NOT_FOUND_REFRESH_TOKEN(9106, "refresh-token에 해당하는 쿠키 정보가 없습니다."),
    INVALID_AUTHORITY(9201, "해당 요청에 대한 접근 권한이 없습니다."),

    INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요.");

    private final int status;
    private final String message;
}
