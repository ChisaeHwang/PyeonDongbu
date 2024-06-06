package com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request;

import com.pyeondongbu.editorrecruitment.domain.common.domain.Skill;
import com.pyeondongbu.editorrecruitment.domain.common.domain.VideoType;
import com.pyeondongbu.editorrecruitment.domain.common.dto.request.DetailsReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPostDetailsReq extends DetailsReq {

    @Builder
    public RecruitmentPostDetailsReq(
            final int maxSubs,
            final List<VideoType> videoTypes,
            final List<Skill> skills,
            final String remarks
    ) {
        super(maxSubs, videoTypes, skills, remarks);
    }

    public static RecruitmentPostDetailsReq of(final RecruitmentPostDetails details) {
        return RecruitmentPostDetailsReq.builder()
                .maxSubs(details.getMaxSubs())
                .videoTypes(details.getVideoTypes())
                .skills(details.getSkills())
                .remarks(details.getRemarks())
                .build();
    }
}
