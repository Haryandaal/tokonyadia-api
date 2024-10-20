package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.response.FileDownloadResponse;
import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.ProductImage;
import com.enigma.tokonyadia_api.repository.ProductImageRepository;
import com.enigma.tokonyadia_api.service.ProductImageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {

    private final Integer SIZE_IMAGE;
    private final ProductImageRepository productImageRepository;
    private final Path path;

    @Autowired
    public ProductImageServiceImpl(
            @Value("${haryanda.tokonyadia.menu-image-path}") String PATH_IMAGE,
            @Value("${haryanda.tokonyadia.menu-image-size}") Integer SIZE_IMAGE,
            ProductImageRepository productImageRepository
    ) {
        this.SIZE_IMAGE = SIZE_IMAGE;
        this.productImageRepository = productImageRepository;
        this.path = Paths.get(PATH_IMAGE).normalize();
    }

    @PostConstruct
    private void initDirectory() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (IOException e) {
                log.error("Error while init directory: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while init directory");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ProductImage> saveImageBulk(List<MultipartFile> multipartFiles, Product product) {
        return multipartFiles.stream().map(multipartFile -> saveImage(multipartFile, product)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductImage saveImage(MultipartFile multipartFile, Product product) {
        try {
            if (multipartFile.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image can't be empty");

            if (multipartFile.getOriginalFilename() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "filename can't be empty");

            if (multipartFile.getSize() >= SIZE_IMAGE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file size exceed limit");
            }

            if (!List.of("image/jpg", "image/jpeg", "image/png", "image/webp").contains(multipartFile.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid extensions type");
            }

            String originalFilename = multipartFile.getOriginalFilename();
            if (!(originalFilename.endsWith(".png") || originalFilename.endsWith(".jpeg") ||
                    originalFilename.endsWith(".jpg") || originalFilename.endsWith(".webp"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid extensions type");
            }


            // make unique name in the server
            String filename = UUID.randomUUID() + "_" + originalFilename;

            Path filePath = path.resolve(filename).normalize();

            Files.copy(multipartFile.getInputStream(), filePath);

            Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));

            ProductImage productImage = ProductImage.builder()
                    .filename(filename)
                    .contentType(multipartFile.getContentType())
                    .path(filePath.toString())
                    .size(multipartFile.getSize())
                    .product(product)
                    .build();
            productImageRepository.saveAndFlush(productImage);
            return productImage;
        } catch (IOException e) {
            log.error("Error while saving image: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while saving image");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public FileDownloadResponse getById(String imageId) {
        try {
            ProductImage productImage = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

            Path filePath = Paths.get(productImage.getPath());
            if (!Files.exists(filePath)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
            Resource resource = new UrlResource(filePath.toUri());
            return FileDownloadResponse.builder()
                    .resource(resource)
                    .contentType(productImage.getContentType())
                    .build();
        } catch (Exception e) {
            log.error("error while read image: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteById(String imageId) {

    }
}
