package com.pyeondongbu.editorrecruitment.domain.tag.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResDTO {

    private List<String> tagNames;


    public static TagResDTO of(
            final List<String> tagNames
    ) {
        return new TagResDTO(tagNames);
    }

}
