package com.pyeondongbu.editorrecruitment.domain.image.domain;

import static java.io.InputStream.nullInputStream;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import com.pyeondongbu.editorrecruitment.global.exception.ImageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ImageFileTest {


    @DisplayName("이미지 파일이 Null아면 예외 처리")
    @Test
    void validateNullImage() throws IOException {
        // given
        final MockMultipartFile nullFile = new MockMultipartFile(
                "images",
                null,
                null,
                nullInputStream()
        );

        // when & then
        assertThatThrownBy(() -> new ImageFile(nullFile))
                .isInstanceOf(ImageException.class)
                .extracting("status")
                .isEqualTo(5002);
    }
}