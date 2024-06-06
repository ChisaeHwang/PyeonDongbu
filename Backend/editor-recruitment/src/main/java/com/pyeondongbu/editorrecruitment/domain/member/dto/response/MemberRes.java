package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberRes {
    private Long id;
    private String socialLoginId;
    private String nickname;
    private String imageUrl;
    private String status;
    private String lastLoginDate;
    private String createdAt;
    private String modifiedAt;

    public MemberRes(Member member) {
        this.id = member.getId();
        this.socialLoginId = member.getSocialLoginId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.status = member.getStatus().toString();
        this.lastLoginDate = member.getLastLoginDate().toString();
        this.createdAt = member.getCreatedAt().toString();
        this.modifiedAt = member.getModifiedAt().toString();
    }
}
