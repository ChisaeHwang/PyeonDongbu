package com.pyeondongbu.editorrecruitment.domain.recruitment.dao;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long>, JpaSpecificationExecutor<RecruitmentPost> {
    List<Long> findPostIdsByMemberId(final Long memberId);

    List<RecruitmentPost> findByMemberId(Long memberId);

    Optional<RecruitmentPost> findByMemberIdAndId(Long memberId, Long postId);

    void deleteByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM RecruitmentPost p WHERE p.id IN :postIds")
    void deleteByPostIds(@Param("postIds") final List<Long> postIds);
}
