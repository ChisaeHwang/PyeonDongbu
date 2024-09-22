package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RecruitmentPostDetailsRes {

    private int maxSubs;
    private Set<String> videoTypes;
    private Set<String> skills;
    private String remarks;

    public static RecruitmentPostDetailsRes from(
            final Details details
    ) {
        return new RecruitmentPostDetailsRes(
                details.getMaxSubs(),
                details.getVideoTypes(),
                details.getSkills(),
                details.getRemarks()
        );
    }

    public RecruitmentPostDetails toEntity() {
        return RecruitmentPostDetails.builder()
                .maxSubs(this.maxSubs)
                .videoTypes(this.videoTypes)
                .skills(this.skills)
                .remarks(this.remarks)
                .build();
    }
}
