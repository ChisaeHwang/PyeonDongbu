package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.MemberTokens;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.config.WebConfig;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecruitmentPostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class RecruitmentPostControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RecruitmentPostService postService;

    private Set<PaymentDTO> createPaymentDTOSet() {
        Set<PaymentDTO> paymentDTOSet = new HashSet<>();
        paymentDTOSet.add(new PaymentDTO(PaymentType.MONTHLY_SALARY, "5000"));
        paymentDTOSet.add(new PaymentDTO(PaymentType.PER_PROJECT, "50"));
        return paymentDTOSet;
    }

    @BeforeEach
    void setUp() {
        // 기존 설정
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        // 포스트 생성 로직
        RecruitmentPostReq request = RecruitmentPostReq.of(
                null
        );

        Long memberId = 1L; // 적절한 멤버 ID 설정 필요

        RecruitmentPostRes response = RecruitmentPostRes.builder()
                .id(1L)
                .title("샘플 게시글 제목")
                .content("이것은 샘플 게시글 내용입니다.")
                .authorName("작성자")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .images(Arrays.asList("image1.jpg", "image2.jpg"))
                .tagNames(Arrays.asList("태그1", "태그2"))
                .payments((List<PaymentDTO>) createPaymentDTOSet()) // payments 필드 추가
                .build();

        when(postService.create(request, memberId)).thenReturn(response);
    }

    @DisplayName("글쓰기 가능")
    @Test
    void createPost() throws Exception {
        // given
        RecruitmentPostReq request = RecruitmentPostReq.of(
                null
        );

        RecruitmentPostRes response = RecruitmentPostRes.builder()
                .id(1L)
                .title("샘플 게시글 제목")
                .content("이것은 샘플 게시글 내용입니다.")
                .authorName("작성자")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .images(Arrays.asList("image1.jpg", "image2.jpg"))
                .tagNames(Arrays.asList("태그1", "태그2"))
                .payments((List<PaymentDTO>) createPaymentDTOSet()) // payments 필드 추가
                .build();

        when(postService.create(any(RecruitmentPostReq.class), any())).thenReturn(response);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/api/recruitment/posts")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .content(objectMapper.writeValueAsString(request)));

        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("payments")
                                        .type(JsonFieldType.ARRAY)
                                        .description("급여 정보 배열")
                                        .attributes(field("constraint", "객체 배열")),
                                fieldWithPath("payments[].type")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("payments[].amount")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 금액")
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
                                fieldWithPath("data.id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 ID")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("data.title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.authorName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 이름")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("작성일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data.modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("수정일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data.images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("data.tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.payments")
                                        .type(JsonFieldType.ARRAY)
                                        .description("급여 정보 배열")
                                        .attributes(field("constraint", "객체 배열")),
                                fieldWithPath("data.payments[].type")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.payments[].amount")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 금액")
                                        .attributes(field("constraint", "문자열"))
                        )
                )).andReturn();

        // then
        final String actual = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("data").path("title").asText();

        final String expected = response.getTitle();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("게시글 수정 가능")
    @Test
    void updatePost() throws Exception {
        // given
        RecruitmentPostReq updateRequest = RecruitmentPostReq.of(
                null
        );

        RecruitmentPostRes updateResponse = RecruitmentPostRes.builder()
                .id(1L)
                .title("수정된 게시글 제목")
                .content("수정된 내용입니다.")
                .authorName("작성자")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .images(Arrays.asList("image3.jpg", "image4.jpg"))
                .tagNames(Arrays.asList("태그3", "태그4"))
                .payments((List<PaymentDTO>) createPaymentDTOSet()) // payments 필드 추가
                .build();

        when(postService.update(any(Long.class), any(RecruitmentPostReq.class), any())).thenReturn(updateResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(put("/api/recruitment/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print());

        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("payments")
                                        .type(JsonFieldType.ARRAY)
                                        .description("급여 정보 배열")
                                        .attributes(field("constraint", "객체 배열")),
                                fieldWithPath("payments[].type")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("payments[].amount")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 금액")
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
                                fieldWithPath("data.id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 ID")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("data.title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.authorName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 이름")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("작성일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data.modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("수정일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data.images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("data.tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data.payments")
                                        .type(JsonFieldType.ARRAY)
                                        .description("급여 정보 배열")
                                        .attributes(field("constraint", "객체 배열")),
                                fieldWithPath("data.payments[].type")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data.payments[].amount")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 금액")
                                        .attributes(field("constraint", "문자열"))
                        )
                )).andReturn();

        // then
        final String actual = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("data").path("title").asText();

        final String expected = updateResponse.getTitle();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("게시글 삭제 가능")
    @Test
    void deletePost() throws Exception {
        // given
        doNothing().when(postService).deletePost(any(), any());

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/recruitment/posts/{postId}", 1L)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNoContent()).andDo(restDocs.document(
                pathParameters(
                        parameterWithName("postId")
                                .description("게시글 Id")
                )
        ));
    }
}