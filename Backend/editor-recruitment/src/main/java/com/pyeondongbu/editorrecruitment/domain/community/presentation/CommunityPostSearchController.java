package com.pyeondongbu.editorrecruitment.domain.community.presentation;

import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.community.service.CommunityPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts/search")
@RequiredArgsConstructor
@Slf4j
public class CommunityPostSearchController {

    private final CommunityPostService communityPostService;

    @GetMapping("/by-tags")
    public ResponseEntity<ApiResponse<Page<CommunityPostRes>>> searchPosts(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tagNames", required = false) List<String> tagNames,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<CommunityPostRes> posts = communityPostService.searchCommunityPosts(
                search,
                tagNames,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CommunityPostRes>>> getPopularPosts(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        List<CommunityPostRes> posts = communityPostService.getPopularPosts(limit);
        log.info("API Response: {}", posts); // 로그 추가
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }


}
