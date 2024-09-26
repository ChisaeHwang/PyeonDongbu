package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitmentPostReq {

    @NotBlank(message = "제목은 공백이 될 수 없습니다.")
    @Size(max = 20, message = "제목은 20자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 공백이 될 수 없습니다.")
    private String content;

    private String imageUrl;

    private List<String> tagNames;

    @Valid
    private PaymentDTO payment;

    private RecruitmentPostDetailsReq recruitmentPostDetailsReq;

    public static RecruitmentPostReq of(final RecruitmentPost recruitmentPost) {
        return RecruitmentPostReq.builder()
                .title(recruitmentPost.getTitle())
                .content(recruitmentPost.getContent())
                .imageUrl(recruitmentPost.getImageUrl())
                .payment(PaymentDTO.from(recruitmentPost.getPayment()))
                .recruitmentPostDetailsReq(RecruitmentPostDetailsReq.of(recruitmentPost.getDetails()))
                .build();
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public RecruitmentPostDetailsReq getRecruitmentPostDetailsReq() {
        return recruitmentPostDetailsReq;
    }
}