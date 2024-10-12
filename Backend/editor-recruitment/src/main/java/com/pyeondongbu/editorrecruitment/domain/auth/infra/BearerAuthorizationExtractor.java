package com.pyeondongbu.editorrecruitment.domain.auth.infra;

import com.pyeondongbu.editorrecruitment.global.exception.InvalidJwtException;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_ACCESS_TOKEN;

@Slf4j
@Component
public class BearerAuthorizationExtractor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer ";
    public static final String TOKEN_PREFIX = "Bearer ";

    public String extractAccessToken(String header) {
        log.debug("헤더에서 액세스 토큰 추출 : {}", header);


        if (header == null) {
            log.warn("인증 헤더가 null");
            return null;
        }

        if (header.startsWith(BEARER_TYPE)) {
            String token = header.substring(TOKEN_PREFIX.length());
            log.debug("추출된 액세스 토큰: {}", token);
            return token;
        }

        log.warn("Invalid authorization header format: {}", header);
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}