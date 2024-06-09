package com.pyeondongbu.editorrecruitment.domain.member.api;

import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MemberRes;
import com.pyeondongbu.editorrecruitment.domain.member.dto.response.MyPageRes;
import com.pyeondongbu.editorrecruitment.domain.member.service.MemberService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberSearchController {

    private final MemberService memberService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MyPageRes>>> searchMembers(
            @RequestParam(name = "maxSubs", required = false) Integer maxSubs,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "skill", required = false) List<String> skills,
            @RequestParam(name = "videoType", required = false) List<String> videoTypes
    ) {
        List<MyPageRes> members = memberService.searchMembers(
                maxSubs,
                role,
                skills,
                videoTypes
        );
        return ResponseEntity.ok(ApiResponse.success(members, 200));
    }
}
