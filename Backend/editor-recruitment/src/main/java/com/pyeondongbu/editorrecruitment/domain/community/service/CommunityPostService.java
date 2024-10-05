package com.pyeondongbu.editorrecruitment.domain.community.service;

import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;
import com.pyeondongbu.editorrecruitment.domain.recruitment.dto.response.RecruitmentPostRes;

import java.util.List;

public interface CommunityPostService {

    CommunityPostRes create(final CommunityPostReq request, final Long memberId);

    CommunityPostRes getPost(final Long postId, final String remoteAddr, final Long memberId);

    List<CommunityPostRes> getMyPosts(final Long memberId);

    List<CommunityPostRes> listPosts();

    List<CommunityPostRes> searchPostsByTags(List<String> tagNames);

    CommunityPostRes update(final Long postId, final CommunityPostReq request, final Long memberId);

    void deletePost(final Long postId, final Long memberId);

}
