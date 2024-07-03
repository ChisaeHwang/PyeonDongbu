package com.pyeondongbu.editorrecruitment.domain.auth.dto;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class LoginRes {

    private final String accessToken;
    private final String refreshToken;
    private final Integer code;
    private final String message;

    public static LoginRes from(
            final String accessToken,
            final String refreshToken,
            final Integer code,
            final String message
    ) {
        return new LoginRes(
                accessToken,
                refreshToken,
                code,
                message
        );
    }

}
