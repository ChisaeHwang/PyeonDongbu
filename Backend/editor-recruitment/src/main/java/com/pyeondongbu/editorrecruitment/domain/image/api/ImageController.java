package com.pyeondongbu.editorrecruitment.domain.image.api;

import com.pyeondongbu.editorrecruitment.domain.image.dto.response.ImageRes;
import com.pyeondongbu.editorrecruitment.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageRes> uploadImage(@RequestPart("images") List<MultipartFile> images) {
        final ImageRes imagesResponse = imageService.save(images);
        final String firstImageName = imagesResponse.getImageNames().get(0);
        return ResponseEntity.created(URI.create(firstImageName)).body(imagesResponse);
    }
}
