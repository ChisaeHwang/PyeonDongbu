package com.pyeondongbu.editorrecruitment.domain.community.service;

import com.pyeondongbu.editorrecruitment.domain.community.dto.request.CommunityPostReq;
import com.pyeondongbu.editorrecruitment.domain.community.dto.response.CommunityPostRes;

import java.util.List;

public interface CommunityPostService {

    CommunityPostRes create(final CommunityPostReq request, final Long memberId);

    CommunityPostRes getPost(final Long postId, final String remoteAddr);

    List<CommunityPostRes> listPosts();

    CommunityPostRes update(final Long postId, final CommunityPostReq request, final Long memberId);

    void deletePost(final Long postId, final Long memberId);

}
