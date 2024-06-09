package com.pyeondongbu.editorrecruitment.domain.member.dao;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    Optional<Member> findBySocialLoginId(String socialLoginId);

    boolean existsByNickname(String nickname);

    @Modifying
    @Query("""
            UPDATE Member member
            SET member.status = 'DELETED'
            WHERE member.id = :memberId
            """)
    void deleteByMemberId(@Param("memberId") final Long memberId);
}
