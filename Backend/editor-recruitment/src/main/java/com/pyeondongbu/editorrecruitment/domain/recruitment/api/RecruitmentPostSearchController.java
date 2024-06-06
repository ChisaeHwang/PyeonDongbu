package com.pyeondongbu.editorrecruitment.domain.recruitment.api;

import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.PostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/posts/search")
@RequiredArgsConstructor
public class RecruitmentPostSearchController {

    private final RecruitmentPostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostRes>>> searchPosts(
            @RequestParam(name = "keyword") String keyword) {
        List<PostRes> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<PostRes>>> searchPostsByTags(
            @RequestBody @Valid RecruitmentPostTagReq request) {
        List<PostRes> posts = postService.searchPostsByTags(request);
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @GetMapping("/by-details")
    public ResponseEntity<ApiResponse<List<PostRes>>> searchRecruitmentPosts(
            @RequestParam(required = false) Integer maxSubs,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String videoType,
            @RequestParam(required = false) String tagName
    ) {
        List<PostRes> posts = postService.searchRecruitmentPosts(maxSubs, skill, videoType, tagName);
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

}
