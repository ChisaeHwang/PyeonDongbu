package com.pyeondongbu.editorrecruitment.domain.auth.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.infra.provider.GoogleOauthProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleOauthProvider googleOauthProvider;

    @GetMapping("/google")
    public void redirectToGoogleLogin(HttpServletResponse response) {
        String authUrl = googleOauthProvider.getAuthorizationUrl();
        response.setHeader("Location", authUrl);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }
}
