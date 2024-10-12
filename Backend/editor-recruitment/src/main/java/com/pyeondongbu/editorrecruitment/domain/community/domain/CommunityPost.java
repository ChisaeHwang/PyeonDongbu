package com.pyeondongbu.editorrecruitment.domain.community.domain;

import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.validation.PostValidationUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToMany
    @JoinTable(
            name = "community_post_tag",
            joinColumns = @JoinColumn(name = "community_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Builder
    public CommunityPost(
            final String title,
            final String content,
            final Set<Tag> tags,
            final Member member
    ) {
        this.title = title;
        this.content = content;
        this.tags = tags != null ? tags : new HashSet<>();
        this.member = member;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public static CommunityPost of(
            final String title,
            final String content,
            final Set<Tag> tags,
            final Member member
    ) {
        return CommunityPost.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .member(member)
                .build();
    }

    public CommunityPost update(
            final CommunityPostReq req,
            final PostValidationUtils.ValidationResult validationResult
    ) {
        this.title = req.getTitle();
        this.tags = validationResult.tags();
        this.content = req.getContent();
        this.modifiedAt = LocalDateTime.now();
        return this;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}

