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

public interface ImageService {
    ImageRes save(final MultipartFile image);

}
