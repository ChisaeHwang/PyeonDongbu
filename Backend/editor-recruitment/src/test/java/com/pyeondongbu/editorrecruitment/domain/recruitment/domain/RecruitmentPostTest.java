package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;


import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_CONTENT_NULL;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.INVALID_TITLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecruitmentPostTest {

    private static final String TITLE = "SampleTitle";
    private static final String CONTENT = "SampleContent";
    private static final String INVALID_TITLE = null;
    private static final String INVALID_CONTENT = null;

    private Member testMember() {
        return Member.builder()
                .socialLoginId("testSocialLoginId")
                .nickname("testNickname")
                .imageUrl("http://example.com/image.jpg")
                .build();
    }

    @DisplayName("Post 객체를 생성")
    @Test
    void createPost() {
        //given
        final Member member = testMember();

        // when
        final RecruitmentPost post = RecruitmentPost.builder()
                .title(TITLE)
                .content(CONTENT)
                .member(member)
                .build();

        // then
        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo(TITLE);
        assertThat(post.getContent()).isEqualTo(CONTENT);
        assertThat(post.getMember()).isEqualTo(member);
        assertThat(post.getTags()).isEmpty();
    }

    @DisplayName("제목이 null이거나 빈 값이면 예외가 발생")
    @Test
    void validatePost_InvalidTitle() {
        // given
        final Member member = testMember();

        // when & then
        assertThatThrownBy(() -> RecruitmentPost.builder()
                .title(INVALID_TITLE)
                .content(CONTENT)
                .member(member)
                .build())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessage(INVALID_TITLE_NAME.getMessage());

        assertThatThrownBy(() -> RecruitmentPost.builder()
                .title("")
                .content(CONTENT)
                .member(member)
                .build())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessage(INVALID_TITLE_NAME.getMessage());
    }

    @DisplayName("내용이 null이거나 빈 값이면 예외가 발생")
    @Test
    void validatePost_InvalidContent() {
        // given
        final Member member = testMember();

        // when & then
        assertThatThrownBy(() -> RecruitmentPost.builder()
                .title(TITLE)
                .content(INVALID_CONTENT)
                .member(member)
                .build())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessage(INVALID_CONTENT_NULL.getMessage());

        assertThatThrownBy(() -> RecruitmentPost.builder()
                .title(TITLE)
                .content("")
                .member(member)
                .build())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessage(INVALID_CONTENT_NULL.getMessage());
    }

    @DisplayName("Post 객체를 업데이트")
    @Test
    void updatePost() {
        // given
        final Member member = testMember();

        final RecruitmentPost post = RecruitmentPost.builder()
                .title(TITLE)
                .content(CONTENT)
                .member(member)
                .build();

        final String expectedTitle = "UpdatedTitle";
        final String expectedContent = "UpdatedContent";
        final Set<Tag> expectedTags = new HashSet<>();

        // when
        post.update(
                expectedTitle,
                expectedContent,
                expectedTags,
                null
        );

        // then
        assertThat(post.getTitle()).isEqualTo(expectedTitle);
        assertThat(post.getContent()).isEqualTo(expectedContent);
        assertThat(post.getTags()).isEqualTo(expectedTags);
    }

    @DisplayName("Post 객체에 이미지를 추가")
    @Test
    void addImages() {
        // given
        final Member member = testMember();

        final RecruitmentPost post = RecruitmentPost.builder()
                .title(TITLE)
                .content(CONTENT)
                .member(member)
                .build();

        final List<PostImage> images = List.of(
                new PostImage(),
                new PostImage()
        );

        // when
        post.addImages(images);

        // then
        assertThat(post.getImages()).hasSize(2);
        assertThat(post.getImages()).containsAll(images);

    }

    @DisplayName("Post 객체에 태그를 추가")
    @Test
    void addTags() {
        // given
        final Member member = testMember();

        final RecruitmentPost post = RecruitmentPost.builder()
                .title(TITLE)
                .content(CONTENT)
                .member(member)
                .build();

        Set<Tag> tags = Set.of(
                new Tag("Tag1"),
                new Tag("Tag2")
        );

        // when
        post.addTags(tags);

        // then
        assertThat(post.getTags()).hasSize(2);
        assertThat(post.getTags()).containsAll(tags);
    }



}