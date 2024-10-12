package com.pyeondongbu.editorrecruitment.domain.auth.configuration;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.dao.RefreshTokenRepository;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.BearerAuthorizationExtractor;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.JwtProvider;
import com.pyeondongbu.editorrecruitment.global.exception.BadRequestException;
import com.pyeondongbu.editorrecruitment.global.exception.RefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessorResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refresh-token";

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final Auth authAnnotation = parameter.getParameterAnnotation(Auth.class);
        final boolean isRequired = authAnnotation != null && authAnnotation.required();

        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            log.error("유효하지 않은 요청: HttpServletRequest가 null");
            throw new BadRequestException(INVALID_REQUEST);
        }

        try {
            final String refreshToken = extractRefreshToken(request.getCookies());
            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));

            log.info("인증 해결 - 필수 여부: {}, 리프레시 토큰: {}, 액세스 토큰: {}",
                    isRequired, refreshToken != null ? "존재" : "없음", accessToken != null ? "존재" : "없음");

            if (refreshToken == null || accessToken == null) {
                if (isRequired) {
                    log.warn("필수 토큰이 누락됨 - 리프레시 토큰: {}, 액세스 토큰: {}",
                            refreshToken != null ? "존재" : "없음", accessToken != null ? "존재" : "없음");
                    throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
                }
                log.info("토큰 누락으로 인해 게스트 접근자 반환");
                return Accessor.guest();
            }

            jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));

            final Long memberId = Long.parseLong(jwtProvider.getPayload(accessToken));
            log.info("인증된 사용자 (회원 ID: {})", memberId);
            return Accessor.member(memberId);
        } catch (final Exception e) {
            log.error("인증 중 예상치 못한 오류 발생: ", e);
            if (isRequired) {
                throw e;
            }
            log.info("예상치 못한 오류로 인해 게스트 접근자 반환");
            return Accessor.guest();
        }
    }

    private String extractRefreshToken(final Cookie... cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) &&
                refreshTokenRepository.existsById(cookie.getValue());
    }
}