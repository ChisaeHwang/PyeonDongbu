package com.pyeondongbu.editorrecruitment.domain.member.api;

import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.pyeondongbu.editorrecruitment.domain.auth.api.LoginController;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.auth.service.LoginService;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MemberDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberDetailsRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.member.service.MemberServiceImpl;
import com.pyeondongbu.editorrecruitment.global.config.WebConfig;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;


@WebMvcTest(controllers = MemberController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MemberControllerTest extends ControllerTest {

    private final static String REFRESH_TOKEN = "refreshToken";
    private final static String ACCESS_TOKEN = "accessToken";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberServiceImpl memberService;


    private Member createMember() {
        return new Member(
                1L,
                Role.CLIENT,
                "socialLoginId",
                "testNickname",
                "https://pyeondongbu.s3.ap-northeast-2.amazonaws.com/testImage.jpg"
        );
    }

    private MemberDetails createMemberDetails() {
        return MemberDetails.builder()
                .maxSubs(1000)
                .videoTypes(Arrays.asList("Gaming"))
                .editedChannels(Arrays.asList("Channel1", "Channel2"))
                .currentChannels(Arrays.asList("CurrentChannel1"))
                .portfolio("Test Portfolio")
                .skills(Arrays.asList("Skill1", "Skill2"))
                .remarks("Test Remarks")
                .build();
    }

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    @DisplayName("사용자 정보 조회 가능 ")
    @Test
    void getMyInfo() throws Exception {
        // given
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());
        final Member member = createMember();
        final MemberDetails memberDetails = createMemberDetails();
        member.setDetails(memberDetails);
        final MyPageRes expectedResponse = MyPageRes.from(member);
        when(memberService.getMyPage(any()))
                .thenReturn(expectedResponse);

        final ResultActions resultActions = mockMvc.perform(get("/api/member")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        ).andDo(print());

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.nickname")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.imageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필사진 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.role")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 역할")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.maxSubs")
                                        .type(JsonFieldType.NUMBER)
                                        .description("구독자 수")
                                        .attributes(field("constraint", "숫자")),
                                fieldWithPath("data.memberDetailsRes.videoType")
                                        .type(JsonFieldType.STRING)
                                        .description("비디오 타입")
                                        .optional()
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.editedChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("편집한 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.currentChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("현재 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.portfolio")
                                        .type(JsonFieldType.STRING)
                                        .description("포트폴리오")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.skills")
                                        .type(JsonFieldType.ARRAY)
                                        .description("스킬 리스트")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.remarks")
                                        .type(JsonFieldType.STRING)
                                        .description("비고")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));

        // then
        verify(memberService).getMyPage(any());
    }

    @DisplayName("사용자 정보 수정 가능")
    @Test
    void updateMyInfo() throws Exception {
        // given
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());
        final Member member = createMember();
        final MemberDetails memberDetails = createMemberDetails();
        member.setDetails(memberDetails);
        final MyPageReq request = MyPageReq.of(member);
        final MyPageRes expectedResponse = MyPageRes.from(member);

        when(memberService.updateMyPage(any(), any()))
                .thenReturn(expectedResponse);

        final ResultActions resultActions = mockMvc.perform(put("/api/member")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, ACCESS_TOKEN)
                .content(objectMapper.writeValueAsString(request))
                .cookie(cookie)
        ).andDo(print());

        // when
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestFields(
                                fieldWithPath("nickname")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("imageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필사진 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("role")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 역할")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("memberDetails.maxSubs")
                                        .type(JsonFieldType.NUMBER)
                                        .description("구독자 수")
                                        .attributes(field("constraint", "숫자")),
                                fieldWithPath("memberDetails.videoType")
                                        .type(JsonFieldType.STRING)
                                        .description("비디오 타입")
                                        .optional()
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("memberDetails.editedChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("편집한 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("memberDetails.currentChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("현재 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("memberDetails.portfolio")
                                        .type(JsonFieldType.STRING)
                                        .description("포트폴리오")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("memberDetails.skills")
                                        .type(JsonFieldType.ARRAY)
                                        .description("스킬 리스트")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("memberDetails.remarks")
                                        .type(JsonFieldType.STRING)
                                        .description("비고")
                                        .attributes(field("constraint", "문자열"))
                        ),
                        responseFields(
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("응답 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.nickname")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.imageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필사진 URL")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.role")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 역할")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.maxSubs")
                                        .type(JsonFieldType.NUMBER)
                                        .description("구독자 수")
                                        .attributes(field("constraint", "숫자")),
                                fieldWithPath("data.memberDetailsRes.videoType")
                                        .type(JsonFieldType.STRING)
                                        .description("비디오 타입")
                                        .optional()
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.editedChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("편집한 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.currentChannels")
                                        .type(JsonFieldType.ARRAY)
                                        .description("현재 채널")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.portfolio")
                                        .type(JsonFieldType.STRING)
                                        .description("포트폴리오")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.memberDetailsRes.skills")
                                        .type(JsonFieldType.ARRAY)
                                        .description("스킬 리스트")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.memberDetailsRes.remarks")
                                        .type(JsonFieldType.STRING)
                                        .description("비고")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));


        // then
        verify(memberService).updateMyPage(any(), any());
    }
}