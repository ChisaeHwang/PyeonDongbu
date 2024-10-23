package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/posts")
@RequiredArgsConstructor
public class RecruitmentPostController {

    private final RecruitmentPostService postService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<RecruitmentPostRes>> createPost(
            @Auth final Accessor accessor,
            @RequestBody @Valid final RecruitmentPostReq request) {
        return ResponseEntity.ok(
                ApiResponse.success(postService.create(request, accessor.getMemberId()), 200)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<RecruitmentPostRes>> getPost(
            @PathVariable("postId") Long postId,
            @Auth(required = false) final Accessor accessor,
            HttpServletRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {
        String remoteAddr = request.getRemoteAddr();
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            remoteAddr = forwardedFor.split(",")[0].trim();
        }

        RecruitmentPostRes postResponseDTO = postService.getPost(postId, remoteAddr, accessor.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(
                postResponseDTO, 200)
        );
    }


    @GetMapping("/{postId}/edit")
    public ResponseEntity<ApiResponse<RecruitmentPostRes>> getPostForEdit(
            @PathVariable("postId") Long postId,
            @Auth final Accessor accessor) {
        RecruitmentPostRes post = postService.getPostForEdit(postId, accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(post, 200)
        );
    }

    @GetMapping("/me")
    @MemberOnly
    public ResponseEntity<ApiResponse<List<RecruitmentPostRes>>> getMyPosts(
            @Auth final Accessor accessor
    ) {
        List<RecruitmentPostRes> posts = postService.getMyPosts(accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecruitmentPostRes>>> listPosts() {
        List<RecruitmentPostRes> posts = postService.listPosts();
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @PutMapping("/{postId}")
    @MemberOnly
    public ResponseEntity<ApiResponse<RecruitmentPostRes>> updatePost(
            @Auth final Accessor accessor,
            @PathVariable("postId") Long postId,
            @RequestBody @Valid RecruitmentPostReq request) {
        RecruitmentPostRes updatedPost = postService.update(postId, request, accessor.getMemberId());
        return ResponseEntity.ok(
                ApiResponse.success(updatedPost, 200)
        );
    }

    @DeleteMapping("/{postId}")
    @MemberOnly
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Auth final Accessor accessor,
            @PathVariable("postId") Long postId) {
        postService.deletePost(postId, accessor.getMemberId());
        return ResponseEntity.status(204).body(
                ApiResponse.success(null, 204)
        );
    }
}
