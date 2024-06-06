package com.pyeondongbu.editorrecruitment.domain.auth.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.JwtProvider;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.ErrorCode;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import com.pyeondongbu.editorrecruitment.global.exception.TokenInvalidException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String MEMBER_KEY = "memberId";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtProvider jwtProvider;
    private final MemberRepository userRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (isEmptyToken(request)) {
            return true;
        }

        String accessToken = getAccessToken(request);
        String refreshToken = getRefreshToken(request);
        MemberTokens memberTokens = new MemberTokens(refreshToken, accessToken);

        jwtProvider.validateTokens(memberTokens);

        Accessor accessor = getAccessor(memberTokens.getAccessToken());

        validateMember(accessor.getMemberId());
        request.setAttribute(MEMBER_KEY, accessor);

        return true;
    }

    private boolean isEmptyToken(HttpServletRequest request) {
        return !StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    private String getAccessToken(final HttpServletRequest request) {
        String value = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(value) || !value.startsWith(BEARER_TYPE)) {
            throw new TokenInvalidException(NOT_FOUND_ACCESS_TOKEN);
        }

        return value.substring(BEARER_TYPE.length()).trim();
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new TokenInvalidException(NOT_FOUND_REFRESH_TOKEN);
    }


    private Accessor getAccessor(String accessToken) {
        try {

            final Long memberId = Long.parseLong(jwtProvider.getPayload(accessToken));
            return Accessor.member(memberId);
        } catch (final AuthException e) {
            throw new TokenInvalidException(FAIL_TO_VALIDATE_TOKEN);
        }
    }

    private void validateMember(final Long memberId) {
        if (!userRepository.existsById(memberId)) {
            throw new MemberException(INVALID_USER_NAME);
        }
    }
}
