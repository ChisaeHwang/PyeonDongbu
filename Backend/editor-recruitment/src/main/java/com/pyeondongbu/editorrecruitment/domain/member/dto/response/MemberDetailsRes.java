package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter // 어노테이션 선택 정리
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDetailsRes {

    private int maxSubs;
    private Set<String> videoTypes;
    private Set<String> editedChannels;
    private Set<String> currentChannels;
    private String portfolio;
    private Set<String> skills;
    private String remarks;

    public static MemberDetailsRes from(MemberDetails memberDetails) {
        return new MemberDetailsRes(
                memberDetails.getMaxSubs(),
                memberDetails.getVideoTypes(),
                memberDetails.getEditedChannels(),
                memberDetails.getCurrentChannels(),
                memberDetails.getPortfolio(),
                memberDetails.getSkills(),
                memberDetails.getRemarks()
        );
    }

}
