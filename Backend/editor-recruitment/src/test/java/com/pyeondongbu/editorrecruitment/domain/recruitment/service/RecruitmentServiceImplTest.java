package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.common.dao.PostViewRepository;
import com.pyeondongbu.editorrecruitment.domain.common.domain.PostView;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
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
import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostUpdateReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.global.exception.AuthException;
import com.pyeondongbu.editorrecruitment.global.exception.TagException;
import org.springframework.mock.web.MockHttpServletRequest;


@ExtendWith(MockitoExtension.class)
class RecruitmentServiceImplTest {

    @InjectMocks
    private RecruitmentPostServiceImpl postService;

    @Mock
    private RecruitmentPostRepository postRepository;

    @Mock
    private PostImageRepository postImageRepository;
    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MemberRepository memberRepository;

    private Set<PaymentDTO> createPaymentDTOSet() {
        Set<PaymentDTO> paymentDTOSet = new HashSet<>();
        paymentDTOSet.add(new PaymentDTO(PaymentType.MONTHLY_SALARY, "5000"));
        paymentDTOSet.add(new PaymentDTO(PaymentType.PER_PROJECT, "50"));
        return paymentDTOSet;
    }

    private Set<Payment> createPaymentSet() {
        Set<PaymentDTO> paymentDTOSet = createPaymentDTOSet();
        Set<Payment> paymentSet = new HashSet<>();
        for (PaymentDTO paymentDTO : paymentDTOSet) {
            paymentSet.add(paymentDTO.toEntity());
        }
        return paymentSet;
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
        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.of(
                null
        );

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

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(tagRepository.findByNameIn(any())).willReturn(tags);
        given(postRepository.save(any())).willReturn(expectedPost);

        // when
        RecruitmentPostRes actualPostResDTO = postService.create(testPostReqDTO, member.getId());

        // then
        assertThat(actualPostResDTO.getTitle()).isEqualTo(expectedPost.getTitle());
        assertThat(actualPostResDTO.getContent()).isEqualTo(expectedPost.getContent());
        assertThat(actualPostResDTO.getAuthorName()).isEqualTo(member.getNickname());
        assertThat(actualPostResDTO.getTagNames()).containsExactlyInAnyOrder("tag1", "tag2");

    }


    @DisplayName("잘못된 멤버 ID로 게시글 작성 시 예외 발생")
    @Test
    void saveWithInvalidMemberId() {
        // given
        Long invalidMemberId = 999L;
        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.of(
                null
        );

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
                .id(validMemberId)
                .socialLoginId("socialLoginId")
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();

        RecruitmentPostReq testPostReqDTO = RecruitmentPostReq.of(
                null
        );

        Set<Tag> validTags = Set.of(new Tag("tag1"));

        given(memberRepository.findById(validMemberId)).willReturn(Optional.of(member));
        given(tagRepository.findByNameIn(testPostReqDTO.getTagNames())).willReturn(validTags);

        // when & then
        assertThatThrownBy(() -> postService.create(testPostReqDTO, validMemberId))
                .isInstanceOf(TagException.class);
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

        RecruitmentPostUpdateReq updateReqDTO = RecruitmentPostUpdateReq.of(
                null
        );

        Set<Tag> updatedTags = Set.of(
                new Tag("tag2"),
                new Tag("tag3")
        );

        given(postRepository.findByMemberIdAndId(memberId, postId))
                .willReturn(Optional.of(post));
        given(tagRepository.findByNameIn(updateReqDTO.getTagNames()))
                .willReturn(updatedTags);

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

        given(postRepository.findByMemberIdAndId(memberId, postId))
                .willReturn(Optional.of(post));

        // when
        postService.deletePost(postId, memberId);

        // then
        verify(postRepository, times(1)).delete(post);
        given(postRepository.findByMemberIdAndId(memberId, postId)).willReturn(Optional.empty());
        Optional<RecruitmentPost> deletedPost = postRepository.findByMemberIdAndId(memberId, postId);
        assertThat(deletedPost).isEmpty();
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

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.0.1");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postViewRepository.existsById(postId + ":192.168.0.1")).willReturn(false);

        // when
        RecruitmentPostRes actualPostResDTO = postService.getPost(postId, request);

        // then
        assertThat(actualPostResDTO.getTitle()).isEqualTo(post.getTitle());
        assertThat(actualPostResDTO.getContent()).isEqualTo(post.getContent());
        assertThat(actualPostResDTO.getTagNames()).containsExactly("tag1");
        verify(postViewRepository, times(1)).save(any(PostView.class));
        verify(postRepository, times(1)).save(post);
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
        RecruitmentPost post2 = RecruitmentPost.of(
                "title2",
                "content2",
                member,
                Set.of(new Tag("tag2")),
                createPaymentSet()
        );

        given(postRepository.findAll()).willReturn(List.of(post1, post2));

        // when
        List<RecruitmentPostRes> actualPosts = postService.listPosts();

        // then
        assertThat(actualPosts).hasSize(2);
        assertThat(actualPosts).extracting(RecruitmentPostRes::getTitle).containsExactlyInAnyOrder("title1", "title2");
    }


}