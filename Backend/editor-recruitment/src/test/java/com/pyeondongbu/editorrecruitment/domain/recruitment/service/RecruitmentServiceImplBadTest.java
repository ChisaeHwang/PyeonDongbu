package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.global.exception.*;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_IP_ADDRESS;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_PAYMENT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceImplBadTest {

    @InjectMocks
    private RecruitmentPostServiceImpl postService;

    @Mock
    private RecruitmentPostRepository postRepository;

    @Mock
    private RecruitmentPostDetailsRepository postDetailsRepository;


    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostValidationUtils postValidationUtils;

    @DisplayName("잘못된 멤버 ID로 게시글 작성 시 예외 발생")
    @Test
    void saveWithInvalidMemberId() {
        // given
        Long invalidMemberId = 999L;
        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        given(memberRepository.findById(invalidMemberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.create(testPostReqDTO, invalidMemberId))
                .isInstanceOf(AuthException.class);
    }

    @DisplayName("유효하지 않은 태그 이름 포함 시 예외 발생")
    @Test
    void saveWithInvalidTagName() {
        // given
        Long validMemberId = 1L;
        Member member = Member.builder()
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.builder()
                .title("testTitle")
                .content("testContent")
                .tagNames(List.of("invalidTag"))
                .build();

        given(memberRepository.findById(validMemberId)).willReturn(Optional.of(member));
        doThrow(new TagException(INVALID_PAYMENT)).when(postValidationUtils).validateRecruitmentPostReq(testPostReqDTO);

        // when & then
        assertThatThrownBy(() -> postService.create(testPostReqDTO, validMemberId))
                .isInstanceOf(TagException.class);
    }

    @DisplayName("유효하지 않은 결제 정보 포함 시 예외 발생")
    @Test
    void saveWithInvalidPayment() {
        // given
        Long validMemberId = 1L;
        Member member = Member.builder()
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.builder()
                .title("testTitle")
                .content("testContent")
                .build();

        given(memberRepository.findById(validMemberId)).willReturn(Optional.of(member));
        doThrow(new InvalidDomainException(INVALID_PAYMENT)).when(postValidationUtils).validateRecruitmentPostReq(testPostReqDTO);

        // when & then
        assertThatThrownBy(() -> postService.create(testPostReqDTO, validMemberId))
                .isInstanceOf(InvalidDomainException.class);
    }

    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    @Test
    void getNonExistentPost() {
        // given
        Long postId = 999L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then

    }

    @DisplayName("잘못된 IP 주소로 게시글 조회 시 예외 발생")
    @Test
    void getPostWithInvalidIp() {
        // given
        Long postId = 1L;
        RecruitmentPost post = RecruitmentPost.builder()
                .title("title")
                .content("content")
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("999.999.999.999"); // invalid IP

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        doThrow(new BadRequestException(INVALID_IP_ADDRESS))
                .when(postValidationUtils).validatePostView(eq(postId), eq(request.getRemoteAddr()));


    }

    @DisplayName("유효하지 않은 태그 이름으로 게시글 업데이트 시 예외 발생")
    @Test
    void updateWithInvalidTagName() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        RecruitmentPostReq updateReqDTO = RecruitmentPostReq.builder()
                .title("updatedTitle")
                .content("updatedContent")
                .tagNames(List.of("invalidTag"))
                .build();

        RecruitmentPost post = RecruitmentPost.builder()
                .title("originalTitle")
                .content("originalContent")
                .build();

        given(postRepository.findByMemberIdAndId(memberId, postId)).willReturn(Optional.of(post));
        doThrow(new TagException(INVALID_PAYMENT)).when(postValidationUtils).validateRecruitmentPostReq(updateReqDTO);

        // when & then
        assertThatThrownBy(() -> postService.update(postId, updateReqDTO, memberId))
                .isInstanceOf(TagException.class);
    }
}
