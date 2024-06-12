package com.pyeondongbu.editorrecruitment.matching.api;

import com.pyeondongbu.editorrecruitment.matching.domain.MatchingResult;
import com.pyeondongbu.editorrecruitment.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/api/match/{memberId}")
    public ResponseEntity<List<MatchingResult>> getMatchingPosts(
            @PathVariable("memberId") Long memberId,
            @RequestParam(name = "topK", required = false, defaultValue = "10") int limit) {
        List<MatchingResult> results = matchingService.findTopKMatchingPosts(memberId, limit);
        return ResponseEntity.ok(results);
    }
}

