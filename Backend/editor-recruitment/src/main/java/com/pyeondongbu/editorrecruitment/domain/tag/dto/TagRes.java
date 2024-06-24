package com.pyeondongbu.editorrecruitment.domain.tag.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagRes {

    private List<String> tagNames;


    public static TagRes of(
            final List<String> tagNames
    ) {
        return new TagRes(tagNames);
    }

}
