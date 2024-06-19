package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.exception.InvalidDomainException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

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
    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private Set<PostImage> images = new HashSet<>();

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private RecruitmentPostDetails details;

    @ElementCollection
    @CollectionTable(
            name = "recruitment_post_payment",
            joinColumns = @JoinColumn(name = "recruitment_post_id"))
    private Set<Payment> payments = new HashSet<>();

    @Builder
    public RecruitmentPost(
            final Long id,
            final String title,
            final String content,
            final Member member,
            final Set<Tag> tags,
            final Set<Payment> payments
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.tags = tags != null ? tags : new HashSet<>();
        this.payments = payments != null ? payments : new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public RecruitmentPost(final Member member) {
        this.member = member;
    }

    public static RecruitmentPost of(
            final String title,
            final String content,
            final Member author,
            final Set<Tag> tags,
            final Set<Payment> payments
    ) {
        return RecruitmentPost.builder()
                .title(title)
                .content(content)
                .member(author)
                .tags(tags)
                .payments(payments)
                .build();
    }

    public RecruitmentPost update(
            final String title,
            final String content,
            final Set<Tag> tags,
            final Set<Payment> payments
    ) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.payments = payments;
        this.modifiedAt = LocalDateTime.now();
        return this;
    }

    public void addImages(List<PostImage> images) {
        this.images.addAll(images);
        images.forEach(image -> image.setPost(this));
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void setDetails(RecruitmentPostDetails details) {
        this.details = details;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void addPayments(Set<Payment> payments) {
        this.payments.addAll(payments);
    }
}
