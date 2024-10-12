package com.pyeondongbu.editorrecruitment.domain.community.dao;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>, JpaSpecificationExecutor<CommunityPost> {

    Optional<CommunityPost> findByMemberIdAndId(final Long memberId, final Long postId);
    List<CommunityPost> findByMemberId(final Long memberId);

    List<CommunityPost> findAll(Specification<CommunityPost> spec);

    @Query("SELECT cp FROM CommunityPost cp ORDER BY cp.viewCount DESC")
    List<CommunityPost> findTopByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT DISTINCT cp FROM CommunityPost cp JOIN cp.tags t WHERE t.name IN :tagNames")
    List<CommunityPost> findByTagNames(@Param("tagNames") Collection<String> tagNames);

}
