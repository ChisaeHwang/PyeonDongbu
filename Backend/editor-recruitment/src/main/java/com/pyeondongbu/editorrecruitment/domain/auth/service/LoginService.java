package com.pyeondongbu.editorrecruitment.domain.auth.service;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginRes;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;

public interface LoginService {

    LoginRes login(final String code);

    Member findOrCreateMember(final String socialLoginId, final String nickname, final String imageUrl);

    Member createMember(final String socialLoginId, final String nickname, final String imageUrl);

    Boolean checkMember(final String socialLoginId);

    String generateRandomFourDigitCode();

    String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader);

    void removeRefreshToken(final String refreshToken);

    void deleteAccount(final Long userId);

}
