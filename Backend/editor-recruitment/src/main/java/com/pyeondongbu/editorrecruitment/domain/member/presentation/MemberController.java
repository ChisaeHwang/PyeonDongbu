package com.pyeondongbu.editorrecruitment.domain.member.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.member.service.MemberService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //TO-DO: 나중에 없애도 될 거 같은 메서드임
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberRes>> findMember(
            @PathVariable("memberId") Long memberId
    ) {
        MemberRes member = memberService.getMember(memberId);
        return ResponseEntity.ok(
                ApiResponse.success(member, 200)
        );
    }

    @GetMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<MyPageRes>> getMyInfo(@Auth final Accessor accessor) {
        final MyPageRes myPageResponse = memberService.getMyPage(accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(myPageResponse, 200)
        );
    }

    @PutMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<MyPageRes>> updateMyInfo(
            @Auth final Accessor accessor,
            @RequestBody @Valid final MyPageReq myPageRequest
    ) {
        final MyPageRes myPageResponse = memberService.updateMyPage(accessor.getMemberId(), myPageRequest);
        return ResponseEntity.status(200).body(
                ApiResponse.success(myPageResponse, 200)
        );
    }

    @GetMapping("/profile/{nickname}")
    public ResponseEntity<ApiResponse<MyPageRes>> getPublicProfile(
            @PathVariable("nickname") String nickname
    ) {
        MyPageRes profile = memberService.getPublicProfile(nickname);
        return ResponseEntity.ok(
                ApiResponse.success(profile, 200)
        );
    }

}
