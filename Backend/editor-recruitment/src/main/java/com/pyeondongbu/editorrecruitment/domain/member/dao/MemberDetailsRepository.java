package com.pyeondongbu.editorrecruitment.domain.member.dao;

import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {
}
