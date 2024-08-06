package com.pyeondongbu.editorrecruitment.domain.auth.presentation;


import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;;
import static com.pyeondongbu.editorrecruitment.global.dto.ResponseMessage.EXIST_LOGIN_CHECK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.AccessTokenRes;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginReq;
import com.pyeondongbu.editorrecruitment.domain.auth.dto.LoginRes;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.JwtProvider;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.global.config.WebConfig;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.pyeondongbu.editorrecruitment.domain.auth.service.LoginService;


@WebMvcTest(controllers = LoginController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class LoginControllerTest extends ControllerTest {

    private final static String REFRESH_TOKEN = "refreshToken";
    private final static String ACCESS_TOKEN = "accessToken";
    private final static String RENEW_ACCESS_TOKEN = "I'mNewAccessToken!";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @Autowired
    private JwtProvider jwtProvider;


    @DisplayName("로그인 가능")
    @Test
    void login() throws Exception {
        // given
        final LoginReq loginRequest = new LoginReq("code");
        final LoginRes loginResponse = new LoginRes(REFRESH_TOKEN, ACCESS_TOKEN, 200, EXIST_LOGIN_CHECK.getMessage());

        when(loginService.login(anyString()))
                .thenReturn(loginResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andDo(print());

        // when
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                         // Provider 추가 할 시 테스트
//                        pathParameters(
//                                parameterWithName("provider")
//                                        .description("로그인 유형")
//                        ),
                        requestFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("인가 코드")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드"),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description(ACCESS_TOKEN)
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ))
                .andReturn();

        final AccessTokenRes expected = new AccessTokenRes(loginResponse.getAccessToken());

        final String accessToken = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        final AccessTokenRes actual = new AccessTokenRes(accessToken);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("최초 로그인 확인")
    @Test
    void first_login() throws Exception {
        // given
        final LoginReq loginRequest = new LoginReq("code");
        final LoginRes loginResponse = new LoginRes(REFRESH_TOKEN, ACCESS_TOKEN, 201, FIRST_LOGIN_CHECK.getMessage());

        when(loginService.login(anyString()))
                .thenReturn(loginResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andDo(print());

        // when
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        // Provider 추가 할 시 테스트
//                        pathParameters(
//                                parameterWithName("provider")
//                                        .description("로그인 유형")
//                        ),
                        requestFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("인가 코드")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드"),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description(ACCESS_TOKEN)
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ))
                .andReturn();


        final String actual = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("code").asText();

        // then
        assertThat(actual).isEqualTo("201");
    }

    @DisplayName("accessToken 재발급을 통한 로그인 연장 확인")
    @Test
    void extendLogin() throws Exception {

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        when(loginService.renewalAccessToken(REFRESH_TOKEN, ACCESS_TOKEN))
                .thenReturn(RENEW_ACCESS_TOKEN);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드"),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data.accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                )).andReturn();

        final AccessTokenRes expected = new AccessTokenRes(memberTokens.getAccessToken());

        final String accessToken = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        final AccessTokenRes actual = new AccessTokenRes(accessToken);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("멤버의 refreshToken을 삭제하고 로그아웃 할 수 있음")
    @Test
    void logout() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(loginService).removeRefreshToken(anyString());

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/logout")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ));

        // then
        verify(loginService).removeRefreshToken(anyString());
    }



}

