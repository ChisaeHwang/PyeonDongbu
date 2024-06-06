package com.pyeondongbu.editorrecruitment.domain.tag.api;

import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagResDTO;
import com.pyeondongbu.editorrecruitment.domain.tag.service.TagService;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<TagResDTO>> getAllTags() {
        return ResponseEntity.ok(
                ApiResponse.success(tagService.getAllTags(), 200)
        );
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<TagResDTO>> getTagsByPostId(
            @PathVariable("postId") Long postId) {
        return ResponseEntity.ok(
                ApiResponse.success(tagService.getTagsByPostId(postId), 200)
        );
    }
}
