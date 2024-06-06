package com.pyeondongbu.editorrecruitment.domain.auth.domain;

import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import java.util.List;
import org.springframework.stereotype.Component;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.NOT_SUPPORTED_OAUTH_SERVICE;

@Component
public class OauthProviders {

    private final List<OauthProvider> providers;

    public OauthProviders(final List<OauthProvider> providers) {
        this.providers = providers;
    }

    public OauthProvider mapping(final String providerName) {
        return providers.stream()
                .filter(provider -> provider.is(providerName))
                .findFirst()
                .orElseThrow(() -> new AuthException(NOT_SUPPORTED_OAUTH_SERVICE));
    }
}
