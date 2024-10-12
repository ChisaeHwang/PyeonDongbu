package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.service.RecruitmentPostService;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.global.dto.ApiResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitment/posts/search")
@RequiredArgsConstructor
public class RecruitmentPostSearchController {

    private final RecruitmentPostService postService;

    // To-BE RequestParam이 너무 많아서 나중에 이걸 리팩토링 해야함
    @GetMapping("/by-details")
    public ResponseEntity<ApiResponse<Page<RecruitmentPostRes>>> searchRecruitmentPosts(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "maxSubs", required = false) String maxSubs,
            @RequestParam(name = "paymentType", required = false) PaymentType paymentType,
            @RequestParam(name = "workload", required = false) String workload,
            @RequestParam(name = "skills", required = false) List<String> skills,
            @RequestParam(name = "videoTypes", required = false) List<String> videoTypes,
            @RequestParam(name = "tagNames", required = false) List<String> tagNames,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<RecruitmentPostRes> posts = postService.searchRecruitmentPosts(
                title,
                maxSubs,
                paymentType,
                workload,
                skills,
                videoTypes,
                tagNames,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(
                ApiResponse.success(posts, 200)
        );
    }


}
