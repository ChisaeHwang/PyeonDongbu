package com.pyeondongbu.editorrecruitment.domain.community.dao;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Optional<CommunityPost> findByMemberIdAndId(final Long memberId, final Long postId);

    List<CommunityPost> findByMemberId(final Long memberId);

    @Query("SELECT DISTINCT cp FROM CommunityPost cp JOIN cp.tags t WHERE t.name IN :tagNames")
    List<CommunityPost> findByTagNames(@Param("tagNames") Collection<String> tagNames);

}
