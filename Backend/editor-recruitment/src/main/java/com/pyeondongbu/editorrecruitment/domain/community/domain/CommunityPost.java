package com.pyeondongbu.editorrecruitment.domain.community.domain;

import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Builder
    public CommunityPost(
            final Long id,
            final String title,
            final String content,
            final Member member
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public static CommunityPost of(
            final String title,
            final String content,
            final Member member
    ) {
        return CommunityPost.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
    }

    public CommunityPost update(
            final CommunityPostReq req
    ) {
        this.title = req.getTitle();
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

