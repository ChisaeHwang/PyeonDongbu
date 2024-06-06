package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.PaymentDTO;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPostUpdateReq implements PostReq {

    @NonNull
    private String title;
    @NonNull
    private String content;

    private List<String> images;
    private List<String> tagNames;

    private RecruitmentPostDetailsReq recruitmentPostDetailsReq;

    @Valid
    private Set<PaymentDTO> payments;

    public static RecruitmentPostUpdateReq of(final RecruitmentPost recruitmentPost) {
        return  RecruitmentPostUpdateReq.builder()
                .title(recruitmentPost.getTitle())
                .content(recruitmentPost.getContent())
                .recruitmentPostDetailsReq(RecruitmentPostDetailsReq.of(recruitmentPost.getDetails()))
                .build();
    }

    @Override
    public List<String> getImages() {
        return this.images;
    }

}
