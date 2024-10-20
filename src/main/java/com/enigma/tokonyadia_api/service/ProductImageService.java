package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.response.FileDownloadResponse;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImage> saveImageBulk(List<MultipartFile> multipartFiles, Product product);

    FileDownloadResponse getById(String imageId);

    void deleteById(String imageId);
}
