package com.pyeondongbu.editorrecruitment.matching.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import com.pyeondongbu.editorrecruitment.matching.dto.MatchingRes;
import com.pyeondongbu.editorrecruitment.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<MatchingRes>> getMatchingPosts(
             @Auth final Accessor accessor
    ) {
        MatchingRes matchingRes = matchingService.findMatchingPosts(accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(matchingRes, 200)
        );
    }
}

