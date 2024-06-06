package com.pyeondongbu.editorrecruitment.domain.member.dto.request;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MyPageReq {

    @NotBlank(message = "닉네임은 공백이 될 수 없습니다.")
    @Size(max = 15, message = "닉네임은 15자를 초과할 수 없습니다.")
    private String nickname;

    @NotBlank(message = "프로필 사진은 필수입니다.")
    private String imageUrl;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    @NotNull(message = "회원 세부 정보는 필수입니다.")
    private MemberDetailsReq memberDetails;

    public static MyPageReq of(final Member member) {
        return MyPageReq.builder()
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .memberDetails(MemberDetailsReq.of(member.getDetails()))
                .build();
    }


}
