package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.response.FileDownloadResponse;
import com.enigma.tokonyadia_api.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api")
public class FileController {

    private final ProductImageService productImageService;

    @GetMapping(path = "images/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable String id) {
        FileDownloadResponse response = productImageService.getById(id);
        String headerValue = String.format("inline; filename=%s", response.getResource().getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getResource());
    }
}
