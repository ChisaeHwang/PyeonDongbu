package com.pyeondongbu.editorrecruitment.domain.community.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.community.service.CommunityPostService;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<CommunityPostRes>> createPost(
            @Auth final Accessor accessor,
            @RequestBody @Valid final CommunityPostReq request) {
        return ResponseEntity.ok(
                ApiResponse.success(communityPostService.create(request, accessor.getMemberId()), 200)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostRes>> getPost(
            @PathVariable("postId") Long postId,
            HttpServletRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {
        String remoteAddr = request.getRemoteAddr();
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            remoteAddr = forwardedFor.split(",")[0].trim();
        }

        CommunityPostRes postResponseDTO = communityPostService.getPost(postId, remoteAddr);
        return ResponseEntity.ok(ApiResponse.success(postResponseDTO, 200));
    }

    @GetMapping("/me")
    @MemberOnly
    public ResponseEntity<ApiResponse<List<CommunityPostRes>>> getMyPosts(
            @Auth final Accessor accessor
    ) {
        List<CommunityPostRes> posts = communityPostService.getMyPosts(accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityPostRes>>> listPosts() {
        List<CommunityPostRes> posts = communityPostService.listPosts();
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @PutMapping("/{postId}")
    @MemberOnly
    public ResponseEntity<ApiResponse<CommunityPostRes>> updatePost(
            @Auth final Accessor accessor,
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CommunityPostReq request) {
        CommunityPostRes updatedPost = communityPostService.update(postId, request, accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(updatedPost, 200)
        );
    }

    @DeleteMapping("/{postId}")
    @MemberOnly
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Auth final Accessor accessor,
            @PathVariable("postId") Long postId) {
        communityPostService.deletePost(postId, accessor.getMemberId());
        return ResponseEntity.status(204).body(
                ApiResponse.success(null, 204)
        );
    }
}
