package com.pyeondongbu.editorrecruitment.domain.auth.dto;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import lombok.*;

@Getter
@RequiredArgsConstructor
public class LoginRes {

    private final String accessToken;
    private final String refreshToken;

    public static LoginRes from(
            final String accessToken,
            final String refreshToken
    ) {
        return new LoginRes(
                accessToken,
                refreshToken
        );
    }

}
