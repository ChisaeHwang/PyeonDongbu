package com.pyeondongbu.editorrecruitment.domain.community.dao;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByMemberIdAndId(final Long memberId, final Long postId);

    List<CommunityPost> findByMemberId(final Long memberId);

}
