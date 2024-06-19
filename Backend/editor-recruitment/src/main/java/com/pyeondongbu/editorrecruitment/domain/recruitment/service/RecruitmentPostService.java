package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;

public interface RecruitmentPostService {

    RecruitmentPostRes create(RecruitmentPostReq request, Long memberId);

    RecruitmentPostRes getPost(Long postId, String remoteAddr);

    List<RecruitmentPostRes> listPosts();

    RecruitmentPostRes update(Long postId, RecruitmentPostReq request, Long memberId);

    void deletePost(Long postId, Long memberId);


    List<RecruitmentPostRes> searchRecruitmentPosts(
            Integer maxSubs
            , String title,
            List<String> skills,
            List<String> videoTypes,
            List<String> tagNames
    );

    List<RecruitmentPostRes> searchRecruitmentPostsByTags(
            List<String> tagNames
    );

}
