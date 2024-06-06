package com.pyeondongbu.editorrecruitment.domain.auth.service;

import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.EXIST_LOGIN_CHECK;
import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.FIRST_LOGIN_CHECK;
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


import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

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

    @Override
    public LoginRes login(String code) {
        try {
            final OauthProvider provider = oauthProviders.mapping("google");
            final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

            final Boolean isExistingMember = checkMember(oauthUserInfo.getSocialLoginId());

            final Member member = findOrCreateMember(
                    oauthUserInfo.getSocialLoginId(),
                    oauthUserInfo.getNickname(),
                    oauthUserInfo.getImageUrl()
            );
            member.updateLastLoginDate();

            final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

            final RefreshToken refreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
            refreshTokenRepository.save(refreshToken);

            if(isExistingMember) { // 이미 존재하는 멤버일 경우
                return new LoginRes(
                        memberTokens.getAccessToken(),
                        memberTokens.getRefreshToken(),
                        200,
                        EXIST_LOGIN_CHECK.getMessage()
                );
            } else {
                return new LoginRes(
                        memberTokens.getAccessToken(),
                        memberTokens.getRefreshToken(),
                        201,
                        FIRST_LOGIN_CHECK.getMessage()
                );
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new AuthException(ALREADY_USED_AUTHORIZATION_CODE);
            } else {
                throw new AuthException(INTERNAL_SEVER_ERROR);
            }
        }
    }

    @Override
    public Member findOrCreateMember(String socialLoginId, String nickname, String imageUrl) {
        return memberRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createMember(socialLoginId, nickname, imageUrl));
    }

    @Override
    public Boolean checkMember(String socialLoginId) {
        return memberRepository.findBySocialLoginId(socialLoginId).isPresent();
    }

    @Override
    @Transactional
    public Member createMember(String socialLoginId, String nickname, String imageUrl) {
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
}
