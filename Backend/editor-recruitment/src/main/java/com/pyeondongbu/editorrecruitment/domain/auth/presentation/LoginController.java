package com.pyeondongbu.editorrecruitment.domain.auth.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.AccessTokenRes;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginReq;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginRes;
import com.pyeondongbu.editorrecruitment.domain.auth.service.LoginService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    public static final int COOKIE_AGE_SECONDS = 504800;

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginRes>> login(
            @RequestBody final LoginReq loginReq,
            final HttpServletResponse response
    ) {
        final LoginRes res = loginService.login(loginReq.getCode());
        final ResponseCookie cookie = ResponseCookie.from("refresh-token", res.getRefreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .secure(false) // https 에서 사용할 경우 수정
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                ApiResponse.success(res, 201)
        );
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<AccessTokenRes>> extendLogin(
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final String renewalAccessToken = loginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.ok(
                ApiResponse.success(new AccessTokenRes(renewalAccessToken), 200)
        );
    }

    @DeleteMapping("/logout")
    @MemberOnly
    public ResponseEntity<ApiResponse<Void>> logout(
            @Auth final Accessor accessor,
            @CookieValue("refresh-token") final String refreshToken) {
        loginService.removeRefreshToken(refreshToken);
        return ResponseEntity.status(204).body(
                ApiResponse.success(null, 204)
        );
    }

    @DeleteMapping("/account")
    @MemberOnly
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Auth final Accessor accessor) {
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.status(204).body(
                ApiResponse.success(null, 204)
        );
    }
}
