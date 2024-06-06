package com.pyeondongbu.editorrecruitment.domain.tag.domain;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {

    private static final String TAG_NAME = "SampleTag";


    @DisplayName("Tag 객체를 생성")
    @Test
    void generateTag() {

        // given & when
        final Tag tag = new Tag(TAG_NAME);

        // then
        assertThat(tag).isNotNull();
        assertThat(tag.getName()).isEqualTo(TAG_NAME);
    }

    @DisplayName("Tag 객체에 Post를 설정")
    @Test
    void setPost() {
        // given
        final Tag tag = new Tag(TAG_NAME);
        final RecruitmentPost post1 = RecruitmentPost.builder()
                .title("Sample Title1")
                .content("Sample Content1")
                .member(Member.builder()
                        .socialLoginId("testSocialLoginId")
                        .nickname("testNickname")
                        .imageUrl("http://example.com/image.jpg")
                        .build())
                .build();

        final RecruitmentPost post2 = RecruitmentPost.builder()
                .title("Sample Title2")
                .content("Sample Content2")
                .member(Member.builder()
                        .socialLoginId("testSocialLoginId")
                        .nickname("testNickname")
                        .imageUrl("http://example.com/image.jpg")
                        .build())
                .build();

        // when
        tag.addRecruitmentPosts(post1);
        tag.addRecruitmentPosts(post2);

        // then
        assertThat(tag.getRecruitmentPosts()).contains(post1, post2);
    }
}