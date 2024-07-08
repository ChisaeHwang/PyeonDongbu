package com.pyeondongbu.editorrecruitment.domain.auth.service;

import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.*;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthProviders;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginRes;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.BearerAuthorizationExtractor;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.JwtProvider;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthProvider;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthUserInfo;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.dao.RefreshTokenRepository;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.RefreshToken;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.MemberDeleteEvent;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;


import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import com.pyeondongbu.editorrecruitment.global.validation.MemberValidationUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;


    private final MemberRepository memberRepository;
    private final RecruitmentPostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;
    private final ApplicationEventPublisher publisher;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberValidationUtils memberValidationUtils;

    @Override
    public LoginRes login(final String code) {
        try {
            final OauthProvider provider = oauthProviders.mapping("google");
            final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

            final Boolean isCheckMember = checkMember(oauthUserInfo.getSocialLoginId());

            final Member member = findOrCreateMember(
                    oauthUserInfo.getSocialLoginId(),
                    oauthUserInfo.getNickname(),
                    oauthUserInfo.getImageUrl()
            );
            member.updateLastLoginDate();

            final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

            final RefreshToken refreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
            refreshTokenRepository.save(refreshToken);

            return createLoginResponse(memberTokens, isCheckMember);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new AuthException(ALREADY_USED_AUTHORIZATION_CODE);
            } else {
                throw new AuthException(INTERNAL_SEVER_ERROR);
            }
        }
    }

    @Override
    public Member findOrCreateMember(final String socialLoginId, final String nickname, final String imageUrl) {
        return memberRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createMember(socialLoginId, nickname, imageUrl));
    }

    @Override
    public Boolean checkMember(final String socialLoginId) {
        final Optional<Member> optionalMember = memberRepository.findBySocialLoginId(socialLoginId);
        if (!optionalMember.isPresent()) {
            return false;
        }
        final Member member = optionalMember.get();
        if (memberValidationUtils.validateMemberDetails(member)) {
            return false;
        }
        return true;
    }


    @Override
    @Transactional
    public Member createMember(final String socialLoginId, final String nickname, final String imageUrl) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            final String RandomName = nickname + generateRandomFourDigitCode();
            if (!memberRepository.existsByNickname(RandomName)) {
                return memberRepository.save(Member.of(
                        socialLoginId,
                        RandomName,
                        imageUrl,
                        Role.GUEST
                ));
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_RANDOM_NICKNAME);
    }


    @Override
    public String generateRandomFourDigitCode() {
        final int randomNumber = (int) (Math.random() * FOUR_DIGIT_RANGE);
        return String.format("%04d", randomNumber);
    }


    @Transactional
    public String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader) {

        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);

        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                    .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
            return jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString());
        }
        if (jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return accessToken;
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
    }


    @Override
    @Transactional
    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Override
    @Transactional
    public void deleteAccount(final Long memberId) {
        final List<Long> postIds = postRepository.findPostIdsByMemberId(memberId);
        postRepository.deleteByPostIds(postIds);
        memberRepository.deleteByMemberId(memberId);
        publisher.publishEvent(new MemberDeleteEvent(postIds, memberId));
    }

    /**
     * Private 함수들
     */

    private LoginRes createLoginResponse(final MemberTokens memberTokens, final Boolean isCheckMember) {
        if (isCheckMember) {
            return LoginRes.from(
                    memberTokens.getAccessToken(),
                    memberTokens.getRefreshToken(),
                    200,
                    EXIST_LOGIN_CHECK.getMessage()
            );
        } else {
            return LoginRes.from(
                    memberTokens.getAccessToken(),
                    memberTokens.getRefreshToken(),
                    201,
                    FIRST_LOGIN_OR_ENTER_DETAILS_CHECK.getMessage()
            );
        }
    }

}
