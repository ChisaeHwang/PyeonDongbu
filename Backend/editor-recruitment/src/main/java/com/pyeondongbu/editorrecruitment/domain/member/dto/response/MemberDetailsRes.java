package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import lombok.*;

import java.util.List;

@Getter // 어노테이션 선택 정리
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberDetailsRes {

    private int maxSubs;
    private List<String> videoTypes;
    private List<String> editedChannels;
    private List<String> currentChannels;
    private String portfolio;
    private List<String> skills;
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
