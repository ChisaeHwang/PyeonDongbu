package com.pyeondongbu.editorrecruitment.domain.recruitment.dao;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.details.RecruitmentPostDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentPostDetailsRepository extends JpaRepository<RecruitmentPostDetails, Long> {
}
