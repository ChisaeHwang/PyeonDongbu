package com.pyeondongbu.editorrecruitment.domain.tag.dao;

import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    Set<Tag> findByNameIn(List<String> names);


    @Query("""
           SELECT t FROM Tag t
           JOIN t.recruitmentPosts p
           WHERE p.id = :postId
           """)
    Set<Tag> findByPostId(@Param("postId") Long postId);

}
