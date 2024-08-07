package com.pyeondongbu.editorrecruitment.domain.community.dao;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByMemberIdAndId(final Long memberId, final Long postId);

}
