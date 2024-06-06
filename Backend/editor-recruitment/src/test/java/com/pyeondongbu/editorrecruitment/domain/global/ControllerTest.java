package com.pyeondongbu.editorrecruitment.domain.global;

import com.pyeondongbu.editorrecruitment.domain.auth.configuration.AccessorResolver;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.dao.RefreshTokenRepository;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.BearerAuthorizationExtractor;
import com.pyeondongbu.editorrecruitment.domain.auth.infra.JwtProvider;
import com.pyeondongbu.editorrecruitment.domain.global.restdocs.RestDocsConfiguration;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccessorResolver accessorResolver;

    @MockBean
    protected JwtProvider jwtProvider;

    @MockBean
    protected MemberRepository memberRepository;

    @MockBean
    protected RecruitmentPostRepository postRepository;

    @MockBean
    protected RefreshTokenRepository refreshTokenRepository;

    @MockBean
    BearerAuthorizationExtractor bearerExtractor;


    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}