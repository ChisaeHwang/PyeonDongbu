package com.pyeondongbu.editorrecruitment.matching.api;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
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
    public ResponseEntity<ApiResponse<List<MatchingResult>>> getMatchingPosts(
            @Auth final Accessor accessor,
            @RequestParam(name = "topK", required = false, defaultValue = "10") int limit) {
        List<MatchingResult> results = matchingService.findTopKMatchingPosts(accessor.getMemberId(), limit);
        return ResponseEntity.ok(
                ApiResponse.success(results, 200)
        );
    }
}

