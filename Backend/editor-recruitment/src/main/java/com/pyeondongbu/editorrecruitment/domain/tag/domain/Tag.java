package com.pyeondongbu.editorrecruitment.domain.tag.domain;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "tags")
    private Set<RecruitmentPost> recruitmentPosts = new HashSet<>();

    @Column(nullable = false, length = 15, unique = true)
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    public void addRecruitmentPosts(RecruitmentPost post) {
        this.recruitmentPosts.add(post);
        post.getTags().add(this);
    }
}
