package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostImageTest {

    private static final String IMAGE_URL = "http://example.com/image.jpg";

    private RecruitmentPost generatePost() {
        return RecruitmentPost.builder()
                .title("Sample Title")
                .content("Sample Content")
                .member(Member.builder()
                        .socialLoginId("testSocialLoginId")
                        .nickname("testNickname")
                        .imageUrl("http://example.com/image.jpg")
                        .build())
                .build();

    }

    @DisplayName("PostImage 객체 생성")
    @Test
    void generatePostImage() {
        // given
        final RecruitmentPost post = generatePost();

        // when
        final PostImage postImage = new PostImage(IMAGE_URL, post);

        // then
        assertThat(postImage).isNotNull();
        assertThat(postImage.getImageUrl()).isEqualTo(IMAGE_URL);
    }


    @DisplayName("PostImage 객체 생성")
    @Test
    void setPost() {
        // given
        final RecruitmentPost post = generatePost();
        final PostImage postImage = new PostImage(IMAGE_URL, post);

        // when
        postImage.setPost(post);

        // then
        assertThat(postImage.getPost()).isEqualTo(post);
    }


    @DisplayName("PostImage 객체 생성")
    @Test
    void setImageUrl() {
        // given
        final String expectedImageUrl = "http://example.com/expected_image.jpg";
        final RecruitmentPost post = generatePost();
        final PostImage postImage = new PostImage(IMAGE_URL, post);

        // when
        postImage.setImageUrl(expectedImageUrl);

        // then
        assertThat(postImage.getImageUrl()).isEqualTo(expectedImageUrl);
    }
}