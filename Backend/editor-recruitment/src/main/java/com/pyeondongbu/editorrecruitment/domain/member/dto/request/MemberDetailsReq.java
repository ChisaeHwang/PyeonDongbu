package com.pyeondongbu.editorrecruitment.domain.member.dto.request;

import com.pyeondongbu.editorrecruitment.domain.common.dto.request.DetailsReq;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetailsReq extends DetailsReq {

    @NotNull(message = "편집된 채널 리스트는 필수입니다.")
    private Set<String> editedChannels;

    @NotNull(message = "현재 채널 리스트는 필수입니다.")
    private Set<String> currentChannels;

    private String portfolio = "";

    @Builder
    public MemberDetailsReq(
            final int maxSubs,
            final Set<String> videoTypes,
            final Set<String> skills,
            final String remarks,
            final Set<String> editedChannels,
            final Set<String> currentChannels,
            final String portfolio
    ) {
        super(maxSubs, videoTypes, skills, remarks);
        this.editedChannels = editedChannels;
        this.currentChannels = currentChannels;
        this.portfolio = portfolio;
    }

    public static MemberDetailsReq of(final MemberDetails memberDetails) {
        return MemberDetailsReq.builder()
                .maxSubs(memberDetails.getMaxSubs())
                .videoTypes(memberDetails.getVideoTypes())
                .editedChannels(memberDetails.getEditedChannels())
                .currentChannels(memberDetails.getCurrentChannels())
                .portfolio(memberDetails.getPortfolio())
                .skills(memberDetails.getSkills())
                .remarks(memberDetails.getRemarks())
                .build();
    }



}
