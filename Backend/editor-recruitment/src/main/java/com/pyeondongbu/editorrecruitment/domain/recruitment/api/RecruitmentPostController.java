package com.pyeondongbu.editorrecruitment.domain.recruitment.api;

import com.pyeondongbu.editorrecruitment.domain.auth.annotation.Auth;
import com.pyeondongbu.editorrecruitment.domain.auth.annotation.MemberOnly;
import com.pyeondongbu.editorrecruitment.domain.auth.domain.access.Accessor;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostUpdateReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.PostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/posts")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentPostController {

    private final RecruitmentPostService postService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<ApiResponse<PostRes>> createPost(
            @Auth final Accessor accessor,
            @RequestBody @Valid final RecruitmentPostReq request) {
        return ResponseEntity.ok(
                ApiResponse.success(postService.create(request, accessor.getMemberId()), 200)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostRes>> getPost(
            @PathVariable("postId") Long postId,
            HttpServletRequest request) {
        PostRes postResponseDTO = postService.getPost(postId, request);
        return ResponseEntity.ok(
                ApiResponse.success(postResponseDTO, 200)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostRes>>> listPosts() {
        List<PostRes> posts = postService.listPosts();
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }

    @PutMapping("/{postId}")
    @MemberOnly
    public ResponseEntity<ApiResponse<PostRes>> updatePost(
            @Auth final Accessor accessor,
            @PathVariable("postId") Long postId,
            @RequestBody @Valid RecruitmentPostUpdateReq request) {
        PostRes updatedPost = postService.update(postId, request, accessor.getMemberId());
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
