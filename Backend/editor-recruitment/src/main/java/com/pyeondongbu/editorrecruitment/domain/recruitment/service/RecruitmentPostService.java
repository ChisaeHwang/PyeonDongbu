package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostUpdateReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.PostRes;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;

public interface RecruitmentPostService {

    PostRes create(RecruitmentPostReq request, Long memberId);

    PostRes getPost(Long postId, HttpServletRequest request);

    List<PostRes> listPosts();

    PostRes update(Long postId, RecruitmentPostUpdateReq request, Long memberId);

    void deletePost(Long postId, Long memberId);

    List<PostRes> searchPosts(String keyword);

    List<PostRes> searchRecruitmentPosts(
            Integer maxSubs,
            String skill,
            String videoType,
            String tagName
    );

    List<PostRes> searchPostsByTags(RecruitmentPostTagReq req);
}
