package com.pyeondongbu.editorrecruitment.domain.tag.service;

import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagResDTO;

import java.util.Set;

public interface TagService {

    TagResDTO getTagsByPostId(Long postId);

    TagResDTO getAllTags();

}
