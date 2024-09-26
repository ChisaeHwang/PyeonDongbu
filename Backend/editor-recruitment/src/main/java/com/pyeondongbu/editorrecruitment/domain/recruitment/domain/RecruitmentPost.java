package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
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

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPost {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "recruitment_post_tag",
            joinColumns = @JoinColumn(name = "recruitment_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "details_id")
    private RecruitmentPostDetails details;

    @Embedded
    private Payment payment;

    @Builder
    public RecruitmentPost(
            final Long id,
            final String title,
            final String content,
            final String imageUrl,
            final Member member,
            final Set<Tag> tags,
            final Payment payment
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.member = member;
        this.tags = tags != null ? tags : new HashSet<>();
        this.payment = payment;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public static RecruitmentPost of(
            final String title,
            final String content,
            final String imageUrl,
            final Member member,
            final Set<Tag> tags,
            final Payment payment
    ) {
        return RecruitmentPost.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .member(member)
                .tags(tags)
                .payment(payment)
                .build();
    }

    public RecruitmentPost update(
            final RecruitmentPostReq req,
            final PostValidationUtils.ValidationResult validationResult
    ) {
        this.title = req.getTitle();
        this.content = req.getContent();
        this.imageUrl = req.getImageUrl();
        this.tags = validationResult.tags();
        this.payment = validationResult.payment();
        this.modifiedAt = LocalDateTime.now();
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void setDetails(RecruitmentPostDetails details) {
        this.details = details;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
