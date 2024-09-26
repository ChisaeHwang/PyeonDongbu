package com.pyeondongbu.editorrecruitment.domain.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRes {

    private String imageNames;

    public static ImageRes of(final String imageUrl) {
        return new ImageRes(imageUrl);
    }

}
