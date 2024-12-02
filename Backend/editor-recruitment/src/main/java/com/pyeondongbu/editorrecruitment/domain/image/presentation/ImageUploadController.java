package com.pyeondongbu.editorrecruitment.domain.image.presentation;

import com.pyeondongbu.editorrecruitment.domain.image.domain.ImageFile;
import com.pyeondongbu.editorrecruitment.domain.image.infra.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageUploadController {

    private final ImageUploader imageUploader;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        ImageFile imageFile = new ImageFile(file);
        return "Uploaded: " + imageUploader.uploadImage(imageFile);
    }
}
