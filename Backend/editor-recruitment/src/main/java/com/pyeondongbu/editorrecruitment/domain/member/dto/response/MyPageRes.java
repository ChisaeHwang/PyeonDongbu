package com.pyeondongbu.editorrecruitment.domain.member.dto.response;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageRes {

    private String nickname;
    private String imageUrl;
    private Role role;
    private MemberDetailsRes memberDetailsRes;


    public static MyPageRes from(
            final Member member
    ) {
        return new MyPageRes(
                member.getNickname(),
                member.getImageUrl(),
                member.getRole(),
                MemberDetailsRes.from(member.getDetails())
        );
    }

    public static MyPageRes from(
            final Member member,
            final MemberDetailsRes memberDetailsRes
    ) {
        return new MyPageRes(
                member.getNickname(),
                member.getImageUrl(),
                member.getRole(),
                memberDetailsRes
        );
    }

}
