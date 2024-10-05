package com.pyeondongbu.editorrecruitment.domain.community.presentation;

import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.community.service.CommunityPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts/search")
@RequiredArgsConstructor
public class CommunityPostSearchController {

    private final CommunityPostService communityPostService;

    @GetMapping("/by-tags")
    public ResponseEntity<ApiResponse<List<CommunityPostRes>>> searchPosts(
            @RequestParam(name = "tagNames", required = false) List<String> tagNames
    ) {
        List<CommunityPostRes> posts = communityPostService.searchPostsByTags(tagNames);
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }
}
