package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostDetailsRes;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecruitmentPostSearchController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class RecruitmentPostSearchControllerTest extends ControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RecruitmentPostService postService;

    private static final Cookie COOKIE = new Cookie("refresh-token", "test-refresh-token");
    private static final String ACCESS_TOKEN = "test-access-token";

    @BeforeEach
    void setUp() {
        RecruitmentPostDetailsRes detailsRes = RecruitmentPostDetailsRes.builder()
                .build();

        RecruitmentPostRes response = RecruitmentPostRes.builder()
                .build();

        given(postService.searchRecruitmentPosts(
                any(), any(),any(),any(), any(), any(), any(), any()))
                .willReturn(null);
    }

    @DisplayName("검색 가능")
    @Test
    void searchPosts() throws Exception {
        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/recruitment/posts/search/by-details")
                .param("maxSubs", "2")
                .param("skill", "Java")
                .param("skill", "Spring")
                .param("videoType", "튜토리얼")
                .param("videoType", "강의")
                .param("tagName", "태그1")
                .param("tagName", "태그2")
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, ACCESS_TOKEN));

        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("액세스 토큰")
                                        .attributes(field("constraint", "문자열(jwt)"))
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
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 ID")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("data[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].content")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 내용")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].authorName")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 이름")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("작성일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("수정일시")
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("data[].tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data[].payments")
                                        .type(JsonFieldType.ARRAY)
                                        .description("급여 정보 배열")
                                        .attributes(field("constraint", "객체 배열")),
                                fieldWithPath("data[].payments[].type")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 타입")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].payments[].amount")
                                        .type(JsonFieldType.STRING)
                                        .description("급여 금액")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].recruitmentPostDetailsRes")
                                        .type(JsonFieldType.OBJECT)
                                        .description("게시글 세부사항")
                                        .attributes(field("constraint", "객체")),
                                fieldWithPath("data[].recruitmentPostDetailsRes.maxSubs")
                                        .type(JsonFieldType.NUMBER)
                                        .description("최대 구독자 수")
                                        .attributes(field("constraint", "양의 정수")),
                                fieldWithPath("data[].recruitmentPostDetailsRes.skills")
                                        .type(JsonFieldType.ARRAY)
                                        .description("필요한 기술")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data[].recruitmentPostDetailsRes.videoTypes")
                                        .type(JsonFieldType.ARRAY)
                                        .description("비디오 타입")
                                        .attributes(field("constraint", "문자열 배열")),
                                fieldWithPath("data[].recruitmentPostDetailsRes.remarks")
                                        .type(JsonFieldType.STRING)
                                        .description("비고")
                                        .attributes(field("constraint", "문자열"))
                        )
                )).andReturn();

        // then
        final String actual = new ObjectMapper()
                .readTree(mvcResult.getResponse().getContentAsString())
                .path("data").get(0).path("title").asText();

        final String expected = "샘플 게시글 제목";

        assertThat(actual).isEqualTo(expected);
    }
}
