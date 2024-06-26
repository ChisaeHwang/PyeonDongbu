package com.pyeondongbu.editorrecruitment.domain.auth.infra.provider;

import java.util.Optional;

import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthAccessToken;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthProvider;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.OauthUserInfo;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.info.GoogleUserInfo;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_AUTHORIZATION_CODE;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_SUPPORTED_OAUTH_SERVICE;

@Component
public class GoogleOauthProvider implements OauthProvider {

    private static final String PROPERTIES_PATH = "${spring.oauth2.provider.google.";
    private static final String PROVIDER_NAME = "google";

    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public GoogleOauthProvider(
            @Value(PROPERTIES_PATH + "client-id}") final String clientId,
            @Value(PROPERTIES_PATH + "client-secret}") final String clientSecret,
            @Value(PROPERTIES_PATH + "redirect-uri}") final String redirectUri,
            @Value(PROPERTIES_PATH + "token-uri}") final String tokenUri,
            @Value(PROPERTIES_PATH + "user-info}") final String userUri
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }

    @Override
    public boolean is(final String name) {
        return PROVIDER_NAME.equals(name);
    }

    @Override
    public OauthUserInfo getUserInfo(final String code) {
        final String accessToken = requestAccessToken(code);
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(headers);

        final ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userInfoRequestEntity,
                GoogleUserInfo.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new AuthException(NOT_SUPPORTED_OAUTH_SERVICE);
    }

    private String requestAccessToken(final String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(clientId, clientSecret);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, httpHeaders);
        final ResponseEntity<OauthAccessToken> accessTokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthAccessToken.class
        );

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> new AuthException(INVALID_AUTHORIZATION_CODE))
                .getAccessToken();
    }

    public String getAuthorizationUrl() {
        String scope = "https://www.googleapis.com/auth/userinfo.email";
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=" + scope +
                "&access_type=offline";
    }

}
