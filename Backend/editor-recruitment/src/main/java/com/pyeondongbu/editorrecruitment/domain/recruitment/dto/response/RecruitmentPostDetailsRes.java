package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Details;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecruitmentPostDetailsRes {

    private int maxSubs;
    private List<String> videoTypes;
    private List<String> skills;
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

}
