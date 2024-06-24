package com.pyeondongbu.editorrecruitment.domain.tag.service;

import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagRes;

public interface TagService {

    TagRes getTagsByPostId(Long postId);

    TagRes getAllTags();

}
