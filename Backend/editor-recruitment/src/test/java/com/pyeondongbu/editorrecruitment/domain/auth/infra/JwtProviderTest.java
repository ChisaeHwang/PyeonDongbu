package com.pyeondongbu.editorrecruitment.domain.auth.infra;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.ExpiredPeriodJwtException;
import com.pyeondongbu.editorrecruitment.global.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JwtProviderTest {

    private static final Long EXPIRATION_TIME = 60000L;
    private static final Long EXPIRED_TIME = 0L;
    private static final String SUBJECT = "SampleSubject";
    private static final String INVALID_SECRET_KEY = "invalidSecretKeyForTestingPurposeOnly12345!";

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Autowired
    JwtProvider jwtProvider;

    private MemberTokens testMemberTokens() {
        return jwtProvider.generateLoginToken(SUBJECT);
    }

    private String testJwt(final Long expirationTime, final String subject, final String secretKey) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(
                        Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    @DisplayName("accessToken과 refreshToken을 생성")
    @Test
    void generateLoginToken() {
        // given
        final MemberTokens memberTokens = testMemberTokens();

        // when & then
        assertThat(jwtProvider.getSubject(memberTokens.getAccessToken())).isEqualTo(SUBJECT);
        assertThat(jwtProvider.getSubject(memberTokens.getRefreshToken())).isNull();
    }

    @DisplayName("accessToken과 refreshToken의 유효성을 검증함")
    @Test
    void validateToken() {
        // given
        final MemberTokens memberTokens = testMemberTokens();

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateTokens(memberTokens));
    }

    @DisplayName("refreshToken이 기한 만료 시 예외 처리 확인")
    @Test
    void refreshToken_ExpiredValidate() {
        // given
        final String refreshToken = testJwt(EXPIRED_TIME, SUBJECT, secretKey);
        final String accessToken = testJwt(EXPIRATION_TIME, SUBJECT, secretKey);
        final MemberTokens memberTokens = new MemberTokens(refreshToken, accessToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(ExpiredPeriodJwtException.class)
                .hasMessage(EXPIRED_PERIOD_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("refreshToken이 올바르지 않은 형식일 경우 예외 처리")
    @Test
    void refreshToken_Invalidate() {
        // given
        final String refreshToken = testJwt(EXPIRATION_TIME, SUBJECT, INVALID_SECRET_KEY);
        final String accessToken = testJwt(EXPIRATION_TIME, SUBJECT, secretKey);
        final MemberTokens memberTokens = new MemberTokens(refreshToken, accessToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage(INVALID_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("accessToken이 기한 만료 시 예외 처리 확인")
    @Test
    void accessToken_ExpiredValidate() {
        // given
        final String refreshToken = testJwt(EXPIRATION_TIME, SUBJECT, secretKey);
        final String accessToken = testJwt(EXPIRED_TIME, SUBJECT, secretKey);
        final MemberTokens memberTokens = new MemberTokens(refreshToken, accessToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(ExpiredPeriodJwtException.class)
                .hasMessage(EXPIRED_PERIOD_ACCESS_TOKEN.getMessage());
    }

    @DisplayName("accessToken이 올바르지 않은 형식일 경우 예외 처리")
    @Test
    void accessToken_Invalidate() {
        // given
        final String refreshToken = testJwt(EXPIRATION_TIME, SUBJECT, secretKey);
        final String accessToken = testJwt(EXPIRATION_TIME, SUBJECT, INVALID_SECRET_KEY);
        final MemberTokens memberTokens = new MemberTokens(refreshToken, accessToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage(INVALID_ACCESS_TOKEN.getMessage());
    }

    @DisplayName("refreshToken이 유효하고 accessToken의 유효기간이 지났을 경우 true 반환")
    @Test
    void isValidRefreshAndInvalidAccess() {
        // given
        final String refreshToken = testJwt(EXPIRATION_TIME, SUBJECT, secretKey);
        final String accessToken = testJwt(EXPIRED_TIME, SUBJECT, secretKey);

        // when & then
        assertThat(jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)).isTrue();
    }

    @DisplayName("refreshToken이 만료되면 예외 처리")
    @Test
    void refreshToken_ExpiredException() {
        // given
        final String refreshToken = testJwt(EXPIRED_TIME, SUBJECT, secretKey);
        final String accessToken = testJwt(EXPIRED_TIME, SUBJECT, secretKey);

        // when & then
        assertThatThrownBy(() -> jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken))
                .isInstanceOf(AuthException.class)
                .hasMessage(EXPIRED_PERIOD_REFRESH_TOKEN.getMessage());
    }




}