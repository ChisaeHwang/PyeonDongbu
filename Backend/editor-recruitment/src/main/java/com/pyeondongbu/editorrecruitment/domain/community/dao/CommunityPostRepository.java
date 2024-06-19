package com.pyeondongbu.editorrecruitment.domain.community.dao;

import com.pyeondongbu.editorrecruitment.domain.community.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
}
