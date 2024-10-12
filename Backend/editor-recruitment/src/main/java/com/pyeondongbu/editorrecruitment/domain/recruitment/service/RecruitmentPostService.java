package com.pyeondongbu.editorrecruitment.domain.recruitment.service;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.request.RecruitmentPostTagReq;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface RecruitmentPostService {

    RecruitmentPostRes create(final RecruitmentPostReq request, final Long memberId);

    RecruitmentPostRes getPost(final Long postId, final String remoteAddr, final Long memberId);

    List<RecruitmentPostRes> getMyPosts(final Long memberId);

    List<RecruitmentPostRes> listPosts();

    RecruitmentPostRes update(final Long postId, final RecruitmentPostReq request, final Long memberId);

    void deletePost(final Long postId, final Long memberId);


    Page<RecruitmentPostRes> searchRecruitmentPosts(
            String title,
            String maxSubs,
            PaymentType paymentType,
            String workload,
            List<String> skills,
            List<String> videoTypes,
            List<String> tagNames,
            Pageable pageable
    );

    List<RecruitmentPostRes> searchRecruitmentPostsByTags(
            final List<String> tagNames
    );

}
