package com.pyeondongbu.editorrecruitment.domain.image.service;

import com.pyeondongbu.editorrecruitment.domain.image.domain.ImageFile;
import com.pyeondongbu.editorrecruitment.domain.image.domain.S3ImageEvent;
import com.pyeondongbu.editorrecruitment.domain.image.dto.response.ImageRes;
import com.pyeondongbu.editorrecruitment.domain.image.infra.ImageUploader;
import com.pyeondongbu.editorrecruitment.global.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.EMPTY_IMAGE;


@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public ImageRes save(final MultipartFile image) {
        validateImage(image);
        final ImageFile imageFile = new ImageFile(image);
        final String imageUrl = uploadImage(imageFile);
        return new ImageRes(imageUrl);
    }

    private String uploadImage(final ImageFile imageFile) {
        try {
            return imageUploader.uploadImage(imageFile);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName()));
            throw e;
        }
    }

    private void validateImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }
}