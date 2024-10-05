package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitmentPostRes {

    private Long id;
    private String title;
    private String content;
    private String memberName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;
    private String imageUrl;
    private List<String> tagNames;
    private PaymentDTO payment;
    private RecruitmentPostDetailsRes recruitmentPostDetailsRes;

    private Boolean isAuthor;

    /**
     * 게시글 하나 조회 시
     */

    public static RecruitmentPostRes from(
            final RecruitmentPost post,
            final Boolean isAuthor
    ) {
        return RecruitmentPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberName(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .imageUrl(post.getImageUrl())
                .tagNames(getTagsNameList(post))
                .payment(PaymentDTO.from(post.getPayment()))
                .recruitmentPostDetailsRes(RecruitmentPostDetailsRes.from(post.getDetails()))
                .isAuthor(isAuthor)
                .build();
    }

    /**
     * 게시글 전체 조회 시
     */

    public static RecruitmentPostRes from(
            final RecruitmentPost post
    ) {
        return RecruitmentPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberName(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .viewCount(post.getViewCount())
                .imageUrl(post.getImageUrl())
                .tagNames(getTagsNameList(post))
                .payment(PaymentDTO.from(post.getPayment()))
                .recruitmentPostDetailsRes(RecruitmentPostDetailsRes.from(post.getDetails()))
                .build();
    }

    public RecruitmentPost toEntity(Member member) {
        RecruitmentPost post = RecruitmentPost.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .member(member)
                .imageUrl(member.getImageUrl())
                .tags(this.tagNames.stream()
                        .map(Tag::new)
                        .collect(Collectors.toSet()))
                .payment(new Payment(
                        this.payment.getType(),
                        this.payment.getAmount()
                ))
                .build();

        if (this.recruitmentPostDetailsRes != null) {
            RecruitmentPostDetails details = this.recruitmentPostDetailsRes.toEntity();
            post.setDetails(details);
        }

        return post;
    }

    private static List<String> getTagsNameList(RecruitmentPost post) {
        return post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

}