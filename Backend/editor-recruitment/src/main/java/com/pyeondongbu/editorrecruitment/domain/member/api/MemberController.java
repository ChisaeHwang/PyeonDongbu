package com.pyeondongbu.editorrecruitment.domain.member.api;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.member.dto.request.MyPageReq;
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

}
