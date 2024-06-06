package com.pyeondongbu.editorrecruitment.domain.tag.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeondongbu.editorrecruitment.domain.global.ControllerTest;
import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagResDTO;
import com.pyeondongbu.editorrecruitment.domain.tag.service.TagService;
import com.pyeondongbu.editorrecruitment.global.config.WebConfig;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
})
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class TagControllerTest extends ControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TagService tagService;


    @DisplayName("모든 태그 조회 가능하다.")
    @Test
    void getAllTags() throws Exception {
        // given
        final TagResDTO res = TagResDTO.of(
                Arrays.asList(
                        "태그1", "태그2", "태그3"
                )
        );

        when(tagService.getAllTags()).thenReturn(res);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/tags")
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
                                fieldWithPath("data.tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열"))
                        )
                ));
    }

    @DisplayName("포스트 ID로 태그 조회 가능하다.")
    @Test
    void getTagsByPostId() throws Exception {
        // given
        final TagResDTO res = TagResDTO.of(
                Arrays.asList(
                        "태그1", "태그2", "태그3"
                )
        );

        when(tagService.getTagsByPostId(any()))
                .thenReturn(res);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/api/tags/post/{postId}", 1L)
                .contentType(APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postId").description("포스트 ID")
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
                                fieldWithPath("data.tagNames")
                                        .type(JsonFieldType.ARRAY)
                                        .description("태그 이름 배열")
                                        .attributes(field("constraint", "문자열 배열"))
                        )
                ));
    }
}
