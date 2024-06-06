package com.pyeondongbu.editorrecruitment.domain.recruitment.dao;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.RecruitmentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<RecruitmentPost, Long> {

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.id IN :imageIds")
    void deleteByIds(@Param("imageIds") List<Long> imageIds);

}
