package com.pyeondongbu.editorrecruitment.domain.recruitment.presentation;

import com.pyeondongbu.editorrecruitment.domain.image.domain.ImageFile;
import com.pyeondongbu.editorrecruitment.domain.image.infra.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploader imageUploader;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        ImageFile imageFile = new ImageFile(file);
        List<ImageFile> imageFiles = List.of(imageFile); // 단일 파일을 리스트로 변환
        List<String> uploadedImageNames = imageUploader.uploadImages(imageFiles);
        return "Uploaded: " + uploadedImageNames.get(0);
    }
}
