package com.pyeondongbu.editorrecruitment.domain.member.service;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;

public interface MemberService {

    Member getMember(final Long memberId);

    MyPageRes getMyPage(final Long memberId);

    MyPageRes updateMyPage(final Long memberId, final MyPageReq myPageReq);


}
