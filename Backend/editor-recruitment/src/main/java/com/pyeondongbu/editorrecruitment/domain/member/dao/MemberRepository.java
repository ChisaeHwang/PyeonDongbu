package com.pyeondongbu.editorrecruitment.domain.member.dao;

import com.pyeondongbu.editorrecruitment.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>  {

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
