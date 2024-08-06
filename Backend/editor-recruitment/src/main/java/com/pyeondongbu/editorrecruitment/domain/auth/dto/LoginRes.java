package com.pyeondongbu.editorrecruitment.domain.auth.dto;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class LoginRes {

    private final String accessToken;
    private final String refreshToken;
    private final Member member;

    public static LoginRes from(
            final String accessToken,
            final String refreshToken,
            final Member member
    ) {
        return new LoginRes(
                accessToken,
                refreshToken,
                member
        );
    }

}
