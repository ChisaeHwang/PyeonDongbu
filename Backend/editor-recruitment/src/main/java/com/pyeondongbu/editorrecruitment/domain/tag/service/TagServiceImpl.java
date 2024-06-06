package com.pyeondongbu.editorrecruitment.domain.tag.service;

import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{

    private final TagRepository tagRepository;

    public TagResDTO getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return TagResDTO.of(tagNames);
    }

    public TagResDTO getTagsByPostId(Long postId) {
        Set<Tag> tags = tagRepository.findByPostId(postId);
        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
        return TagResDTO.of(tagNames);
    }

}
