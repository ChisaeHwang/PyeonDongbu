package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.PostImage;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitmentPostRes {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCount;
    private List<String> images;
    private List<String> tagNames;
    private List<PaymentDTO> payments;
    private RecruitmentPostDetailsRes recruitmentPostDetailsRes;

    public static RecruitmentPostRes from(
            final RecruitmentPost post
    ) {

        return new RecruitmentPostRes(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                post.getViewCount(),
                getImagesUrlList(post),
                getTagsNameList(post),
                getPaymentsList(post),
                RecruitmentPostDetailsRes.from(post.getDetails())
        );
    }

    public RecruitmentPost toEntity(Member member) {
        return RecruitmentPost.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .member(member)
                .tags(this.tagNames.stream()
                        .map(tagName -> new Tag(tagName))
                        .collect(Collectors.toSet()))
                .payments(this.payments.stream()
                        .map(paymentDTO -> new Payment(paymentDTO.getType(), paymentDTO.getAmount()))
                        .collect(Collectors.toSet()))
                .build();
    }

    private static List<String> getImagesUrlList(RecruitmentPost post) {
        return post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }
    private static List<String> getTagsNameList(RecruitmentPost post) {
        return post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    private static List<PaymentDTO> getPaymentsList(RecruitmentPost post) {
        return post.getPayments().stream()
                .map(payment -> new PaymentDTO(payment.getType(), payment.getAmount()))
                .collect(Collectors.toList());
    }
}