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

    List<RecruitmentPost> findByMemberId(final Long memberId);

    Optional<RecruitmentPost> findByMemberIdAndId(final Long memberId, final Long postId);

    void deleteByMemberId(final Long memberId);

    @Modifying
    @Query("DELETE FROM RecruitmentPost p WHERE p.id IN :postIds")
    void deleteByPostIds(@Param("postIds") final List<Long> postIds);

    @Query("SELECT rp FROM RecruitmentPost rp " +
            "LEFT JOIN FETCH rp.details d " +
            "LEFT JOIN FETCH rp.tags t " +
            "LEFT JOIN FETCH rp.payments p " +
            "LEFT JOIN FETCH rp.images i " +
            "LEFT JOIN FETCH d.skills s " +
            "LEFT JOIN FETCH d.videoTypes v " +
            "WHERE rp.id = :id")
    Optional<RecruitmentPost> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT rp FROM RecruitmentPost rp " +
            "LEFT JOIN FETCH rp.details d " +
            "LEFT JOIN FETCH rp.tags t " +
            "LEFT JOIN FETCH rp.images i " +
            "LEFT JOIN FETCH d.skills s " +
            "LEFT JOIN FETCH d.videoTypes v " +
            "WHERE rp.id IN (SELECT DISTINCT rp.id FROM RecruitmentPost rp " +
            "LEFT JOIN rp.payments p)")
    List<RecruitmentPost> findAllWithDetails();

}
