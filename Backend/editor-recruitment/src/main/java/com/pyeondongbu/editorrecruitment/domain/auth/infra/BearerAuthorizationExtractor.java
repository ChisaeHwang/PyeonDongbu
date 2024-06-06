package com.pyeondongbu.editorrecruitment.domain.auth.infra;

import com.pyeondongbu.editorrecruitment.global.exception.InvalidJwtException;
import org.springframework.stereotype.Component;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_ACCESS_TOKEN;

@Component
public class BearerAuthorizationExtractor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer ";
    public static final String TOKEN_PREFIX = "Bearer ";

    public String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}
