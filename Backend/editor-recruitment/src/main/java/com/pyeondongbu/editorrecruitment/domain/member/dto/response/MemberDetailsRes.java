package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import lombok.*;

import java.util.List;

@Getter // 어노테이션 선택 정리
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberDetailsRes {

    private int maxSubs;
    private List<String> videoType;
    private List<String> editedChannels;
    private List<String> currentChannels;
    private String portfolio;
    private List<String> skills;
    private String remarks;

    public static MemberDetailsRes from(MemberDetails memberDetails) {
        return MemberDetailsRes.builder()
                .maxSubs(memberDetails.getMaxSubs())
                .editedChannels(memberDetails.getEditedChannels())
                .currentChannels(memberDetails.getCurrentChannels())
                .portfolio(memberDetails.getPortfolio())
                .skills(memberDetails.getSkills())
                .remarks(memberDetails.getRemarks())
                .build();
    }

}
