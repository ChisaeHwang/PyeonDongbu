package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/posts/search")
@RequiredArgsConstructor
public class RecruitmentPostSearchController {

    private final RecruitmentPostService postService;


    @GetMapping("/by-details")
    public ResponseEntity<ApiResponse<List<RecruitmentPostRes>>> searchRecruitmentPosts(
            @RequestParam(name = "maxSubs", required = false) Integer maxSubs,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "skill", required = false) List<String> skill,
            @RequestParam(name = "videoType", required = false) List<String> videoType,
            @RequestParam(name = "tagName", required = false) List<String> tagName
    ) {
        List<RecruitmentPostRes> posts = postService.searchRecruitmentPosts(
                maxSubs,
                title,
                skill,
                videoType,
                tagName
        );
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

}
