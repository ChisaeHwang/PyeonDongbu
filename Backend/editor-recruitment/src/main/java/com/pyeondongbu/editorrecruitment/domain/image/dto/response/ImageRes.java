package com.pyeondongbu.editorrecruitment.domain.image.dto.response;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRes {

    private List<String> imageNames;

    public static ImageRes of(final List<PostImage> images) {
        return new ImageRes(
                images.stream()
                        .map(PostImage::getImageUrl)
                        .toList());
    }

}
