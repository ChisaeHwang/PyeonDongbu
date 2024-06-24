package com.pyeondongbu.editorrecruitment.domain.tag.service;

import com.pyeondongbu.editorrecruitment.domain.tag.dao.TagRepository;
import com.pyeondongbu.editorrecruitment.domain.tag.domain.Tag;
import com.pyeondongbu.editorrecruitment.domain.tag.dto.TagRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl tagService;
    @Mock
    private TagRepository tagRepository;

    @DisplayName("모든 태그를 조회할 수 있다.")
    @Test
    void getAllTags() {
        // given
        List<Tag> tags = Arrays.asList(
                new Tag("태그1"),
                new Tag("태그2"),
                new Tag("태그3")
        );

        when(tagRepository.findAll()).thenReturn(tags);

        // when
        TagRes tagResDTO = tagService.getAllTags();

        // then
        assertThat(tagResDTO.getTagNames()).containsExactly("태그1", "태그2", "태그3");
    }

    @DisplayName("게시글 ID로 태그를 조회할 수 있다.")
    @Test
    void getTagsByPostId() {
        // given
        Set<Tag> tags = new HashSet<>(Arrays.asList(
                new Tag("태그1"),
                new Tag("태그2"),
                new Tag("태그3")
        ));

        when(tagRepository.findByPostId(anyLong())).thenReturn(tags);

        // when
        TagRes tagResDTO = tagService.getTagsByPostId(1L);

        // then
        assertThat(tagResDTO.getTagNames()).containsExactlyInAnyOrder("태그1", "태그2", "태그3");
    }
}
