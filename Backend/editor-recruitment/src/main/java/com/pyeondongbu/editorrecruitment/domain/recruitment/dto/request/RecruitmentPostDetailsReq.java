package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request;

import com.pyeondongbu.editorrecruitment.domain.common.dto.request.DetailsReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPostDetailsReq extends DetailsReq {

    @Builder
    public RecruitmentPostDetailsReq(
            final int maxSubs,
            final int weeklyWorkload,
            final Set<String> videoTypes,
            final Set<String> skills,
            final String remarks
    ) {
        super(maxSubs, weeklyWorkload, videoTypes, skills, remarks);
    }

    public static RecruitmentPostDetailsReq of(final RecruitmentPostDetails details) {
        return RecruitmentPostDetailsReq.builder()
                .maxSubs(details.getMaxSubs())
                .weeklyWorkload(details.getWeeklyWorkload())
                .videoTypes(details.getVideoTypes())
                .skills(details.getSkills())
                .remarks(details.getRemarks())
                .build();
    }
}
