package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.common.domain.PostView;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostDetailsRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostDetailsReq;
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.pyeondongbu.editorrecruitment.domain.member.dao.MemberRepository;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.PostImageRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dao.RecruitmentPostRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceImplTest {

    @InjectMocks
    private RecruitmentPostServiceImpl postService;

    @Mock
    private RecruitmentPostRepository postRepository;

    @Mock
    private RecruitmentPostDetailsRepository postDetailsRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostValidationUtils postValidationUtils;

    private List<PaymentDTO> createPaymentDTOList() {
        return List.of(
                new PaymentDTO(PaymentType.MONTHLY_SALARY, "5000"),
                new PaymentDTO(PaymentType.PER_PROJECT, "50")
        );
    }

    private Set<Payment> createPaymentSet() {
        List<PaymentDTO> paymentDTOSet = createPaymentDTOList();
        Set<Payment> paymentSet = new HashSet<>();
        for (PaymentDTO paymentDTO : paymentDTOSet) {
            paymentSet.add(paymentDTO.toEntity());
        }
        return paymentSet;
    }

    private RecruitmentPostDetails createRecruitmentPostDetails() {
        return RecruitmentPostDetails.builder()
                .maxSubs(10)
                .remarks("testRemarks")
                .skills(List.of("Java", "Spring"))
                .videoTypes(List.of("Tutorial"))
                .build();
    }

    @DisplayName("새롭게 생성한 게시글의 id를 반환")
    @Test
    void save() {
        // given
        Member member = Member.builder()
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();
        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.builder()
                .title("testTitle")
                .content("testContent")
                .tagNames(List.of("tag1", "tag2"))
                .payments(createPaymentDTOList())
                .recruitmentPostDetailsReq(RecruitmentPostDetailsReq.builder()
                        .maxSubs(10)
                        .skills(List.of("Java", "Spring"))
                        .videoTypes(List.of("Tutorial"))
                        .remarks("testRemarks")
                        .build())
                .build();

        Set<Tag> tags = Set.of(
                new Tag("tag1"),
                new Tag("tag2")
        );

        RecruitmentPost expectedPost = RecruitmentPost.of(
                testPostReqDTO.getTitle(),
                testPostReqDTO.getContent(),
                member, tags,
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails = createRecruitmentPostDetails();
        postDetails.setRecruitmentPost(expectedPost);
        expectedPost.setDetails(postDetails);

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(postValidationUtils.validateRecruitmentPostReq(testPostReqDTO))
                .willReturn(
                        new PostValidationUtils.ValidationResult(
                                tags,
                                createPaymentSet()
                        )
                );
        given(postRepository.save(any())).willReturn(expectedPost);

        // when
        RecruitmentPostRes actualPostResDTO = postService.create(testPostReqDTO, member.getId());

        // then
        assertThat(actualPostResDTO.getTitle()).isEqualTo(expectedPost.getTitle());
        assertThat(actualPostResDTO.getContent()).isEqualTo(expectedPost.getContent());
        assertThat(actualPostResDTO.getAuthorName()).isEqualTo(member.getNickname());
        assertThat(actualPostResDTO.getTagNames()).containsExactlyInAnyOrder("tag1", "tag2");
    }

    @DisplayName("게시글 업데이트")
    @Test
    void updatePost() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPost post = RecruitmentPost.of(
                "originalTitle",
                "originalContent",
                member,
                Set.of(new Tag("tag1")),
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails = createRecruitmentPostDetails();
        postDetails.setRecruitmentPost(post);
        post.setDetails(postDetails);

        RecruitmentPostReq updateReqDTO = RecruitmentPostReq.builder()
                .title("updatedTitle")
                .content("updatedContent")
                .tagNames(List.of("tag2", "tag3"))
                .payments(createPaymentDTOList())
                .recruitmentPostDetailsReq(RecruitmentPostDetailsReq.builder()
                        .maxSubs(20)
                        .skills(List.of("Java", "Spring", "Hibernate"))
                        .videoTypes(List.of("Tutorial", "Demo"))
                        .remarks("updatedRemarks")
                        .build())
                .build();

        Set<Tag> updatedTags = Set.of(
                new Tag("tag2"),
                new Tag("tag3")
        );

        given(postRepository.findByMemberIdAndId(memberId, postId))
                .willReturn(Optional.of(post));
        given(postValidationUtils.validateRecruitmentPostReq(updateReqDTO))
                .willReturn(
                        new PostValidationUtils.ValidationResult(
                                updatedTags,
                                createPaymentSet()
                        )
                );

        // when
        RecruitmentPostRes actualPostResDTO = postService.update(postId, updateReqDTO, memberId);

        // then
        assertThat(actualPostResDTO.getTitle()).isEqualTo(updateReqDTO.getTitle());
        assertThat(actualPostResDTO.getContent()).isEqualTo(updateReqDTO.getContent());
        assertThat(actualPostResDTO.getTagNames()).containsExactlyInAnyOrder("tag2", "tag3");
    }

    @DisplayName("게시글 삭제")
    @Test
    void deletePost() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPost post = RecruitmentPost.of(
                "title",
                "content",
                member,
                Set.of(new Tag("tag1")),
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails = createRecruitmentPostDetails();
        postDetails.setRecruitmentPost(post);
        post.setDetails(postDetails);

        given(postRepository.findByMemberIdAndId(memberId, postId))
                .willReturn(Optional.of(post));

        // when
        postService.deletePost(postId, memberId);

        // then
        verify(postRepository, times(1)).delete(post);
    }

    @DisplayName("게시글 조회 시 조회수가 증가")
    @Test
    void getPost_shouldIncreaseViewCount() {
        // given
        Long postId = 1L;
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPost post = RecruitmentPost.of(
                "title",
                "content",
                member,
                Set.of(new Tag("tag1")),
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails = createRecruitmentPostDetails();
        postDetails.setRecruitmentPost(post);
        post.setDetails(postDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.0.1");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        doNothing().when(postValidationUtils).validatePostView(postId, request);

        // when
        RecruitmentPostRes actualPostResDTO = postService.getPost(postId, request);

        // then
        assertThat(actualPostResDTO.getTitle()).isEqualTo(post.getTitle());
        assertThat(actualPostResDTO.getContent()).isEqualTo(post.getContent());
        assertThat(actualPostResDTO.getTagNames()).containsExactly("tag1");
        verify(postValidationUtils, times(1)).validatePostView(postId, request);
        verify(postRepository, times(1)).save(post);
        verify(postRepository).save(post); // 조회수 증가에 대한 검증 추가
    }

    @DisplayName("모든 게시글 조회")
    @Test
    void listPosts() {
        // given
        Member member = Member.builder()
                .id(1L)
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPost post1 = RecruitmentPost.of(
                "title1",
                "content1",
                member,
                Set.of(new Tag("tag1")),
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails1 = createRecruitmentPostDetails();
        postDetails1.setRecruitmentPost(post1);
        post1.setDetails(postDetails1);

        RecruitmentPost post2 = RecruitmentPost.of(
                "title2",
                "content2",
                member,
                Set.of(new Tag("tag2")),
                createPaymentSet()
        );
        RecruitmentPostDetails postDetails2 = createRecruitmentPostDetails();
        postDetails2.setRecruitmentPost(post2);
        post2.setDetails(postDetails2);

        given(postRepository.findAll()).willReturn(List.of(post1, post2));

        // when
        List<RecruitmentPostRes> actualPosts = postService.listPosts();

        // then
        assertThat(actualPosts).hasSize(2);
        assertThat(actualPosts).extracting(RecruitmentPostRes::getTitle).containsExactlyInAnyOrder("title1", "title2");
    }
}
