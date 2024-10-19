package com.pyeondongbu.editorrecruitment.domain.member.service;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;

import java.util.List;

public interface MemberService {

    MemberRes getMember(final Long memberId);

    MyPageRes getMyPage(final Long memberId);

    MyPageRes updateMyPage(final Long memberId, final MyPageReq myPageReq);

    List<MyPageRes> searchMembers(
            final Integer maxSubs,
            final String role,
            final List<String> skills,
            final List<String> videoTypes
    );

    MyPageRes getPublicProfile(final String nickname);

}
