package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_post_id")
    private RecruitmentPost post;

    private String imageUrl;

    public PostImage(String imageUrl, RecruitmentPost post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }

    public void setPost(RecruitmentPost post) {
        this.post = post;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
