package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import jakarta.servlet.http.HttpServletRequest;


import java.util.List;

public interface RecruitmentPostService {

    RecruitmentPostRes create(final RecruitmentPostReq request, final Long memberId);

    RecruitmentPostRes getPost(final Long postId, final String remoteAddr, final Long memberId);

    List<RecruitmentPostRes> getMyPosts(final Long memberId);

    List<RecruitmentPostRes> listPosts();

    RecruitmentPostRes update(final Long postId, final RecruitmentPostReq request, final Long memberId);

    void deletePost(final Long postId, final Long memberId);


    List<RecruitmentPostRes> searchRecruitmentPosts(
            final String title,
            final String maxSubs,
            final PaymentType paymentType,
            final String workload,
            final List<String> skills,
            final List<String> videoTypes,
            final List<String> tagNames
    );

    List<RecruitmentPostRes> searchRecruitmentPostsByTags(
            final List<String> tagNames
    );

}
