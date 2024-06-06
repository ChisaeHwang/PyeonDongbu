package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import lombok.*;

@Getter // 어노테이션 선택 정리
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MyPageRes {

    private String nickname;
    private String imageUrl;
    private Role role;
    private MemberDetailsRes memberDetailsRes;

    public static MyPageRes from(
            final Member member
    ) {
        return MyPageRes.builder()
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .build();
    }

    public static MyPageRes from(
            final Member member,
            final MemberDetailsRes memberDetailsRes
    ) {
        return MyPageRes.builder()
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .memberDetailsRes(memberDetailsRes)
                .build();
    }

}
