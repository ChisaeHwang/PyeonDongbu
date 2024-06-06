package com.pyeondongbu.editorrecruitment.domain.recruitment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.PostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.config.WebConfig;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecruitmentPostSearchController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class RecruitmentPostSearchControllerTest extends ControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecruitmentPostService postService;

    @DisplayName("키워드로 게시글 검색 가능하다.")
    @Test
    void searchPosts() throws Exception {
        // given
        final List<PostRes> res = Arrays.asList(
                PostRes.builder()
                        .id(1L)
                        .title("샘플 제목 1")
                        .content("샘플 내용 1")
                        .build(),
                PostRes.builder()
                        .id(2L)
                        .title("샘플 제목 2")
                        .content("샘플 내용 2")
                        .build()
        );

        when(postService.searchPosts(any())).thenReturn(res);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/recruitment/posts/search")
                        .param("keyword", "샘플")
                        .contentType(APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
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
                                        .optional()
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("작성일시")
                                        .optional()
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("수정일시")
                                        .optional()
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .optional()
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("data[].tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .optional()
                                        .attributes(field("constraint", "문자열 배열"))
                        )
                ));
    }

    @DisplayName("태그로 게시글 검색 가능")
    @Test
    void searchPostsByTags() throws Exception {
        // given
        final List<PostRes> res = Arrays.asList(
                PostRes.builder()
                        .id(1L)
                        .title("샘플 제목 1")
                        .content("샘플 내용 1")
                        .build(),
                PostRes.builder().id(2L)
                        .title("샘플 제목 2")
                        .content("샘플 내용 2")
                        .build()
        );

        when(postService.searchPostsByTags(any(RecruitmentPostTagReq.class))).thenReturn(res);

        // when
        RecruitmentPostTagReq request = RecruitmentPostTagReq.of(Arrays.asList("태그1", "태그2"));

        final ResultActions resultActions = mockMvc.perform(get("/api/recruitment/posts/search/tags")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열"))
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
                                        .optional()
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("작성일시")
                                        .optional()
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].modifiedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("수정일시")
                                        .optional()
                                        .attributes(field("constraint", "ISO 8601 날짜/시간")),
                                fieldWithPath("data[].images")
                                        .type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 배열")
                                        .optional()
                                        .attributes(field("constraint", "URL 배열")),
                                fieldWithPath("data[].tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .optional()
                                        .attributes(field("constraint", "문자열 배열"))
                        )
                ));
    }
}
