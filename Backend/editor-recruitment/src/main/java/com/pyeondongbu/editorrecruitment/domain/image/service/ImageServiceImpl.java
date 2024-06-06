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

import java.util.List;

import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.EMPTY_IMAGE_LIST;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.EXCEED_IMAGE_LIST_SIZE;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final int MAX_IMAGE_LIST_SIZE = 5;
    private static final int EMPTY_LIST_SIZE = 0;

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public ImageRes save(final List<MultipartFile> images) {
        validateSizeOfImages(images);
        final List<ImageFile> imageFiles = images.stream()
                .map(ImageFile::new)
                .toList();
        final List<String> imageNames = uploadImages(imageFiles);
        return new ImageRes(imageNames);
    }

    private List<String> uploadImages(final List<ImageFile> imageFiles) {
        try {
            return imageUploader.uploadImages(imageFiles);
        } catch (final ImageException e) {
            imageFiles.forEach(imageFile -> publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName())));
            throw e;
        }
    }

    private void validateSizeOfImages(final List<MultipartFile> images) {
        if (images.size() > MAX_IMAGE_LIST_SIZE) {
            throw new ImageException(EXCEED_IMAGE_LIST_SIZE);
        }
        if (images.size() == EMPTY_LIST_SIZE) {
            throw new ImageException(EMPTY_IMAGE_LIST);
        }
    }

}
