package com.pyeondongbu.editorrecruitment.global.config.redis;

import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_METHOD_LOCK_KEY;

@Getter
@RequiredArgsConstructor
public enum RedisKey {

    SEARCH_KEYWORD("SEARCH::keyword"),
    VIEWS_RECRUITMENT_POST("VIEWS::recruitment-post"),
    LOCK_RECRUITMENT_POST_VIEW("LOCK::recruitment-post-view");

    private final String value;

    public static RedisKey getLockByMethodName(String method) {
        return switch (method) {
            case "getPost" -> LOCK_RECRUITMENT_POST_VIEW;
            default -> throw new BadRequestException(INVALID_METHOD_LOCK_KEY);
        };
    }
}
