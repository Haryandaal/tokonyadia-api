package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.ProductRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WebResponse<ProductResponse>> createProduct(
            @RequestParam(name = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(name = "product") String product
    ) {
        try {
            ProductRequest request = objectMapper.readValue(product, ProductRequest.class);
            ProductResponse productResponse = productService.create(multipartFiles, request);
            return ResponseUtil.buildResponse(HttpStatus.CREATED, "Product created", productResponse);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @GetMapping(path = "categories/{id}")
    public ResponseEntity<?> getProductByCategoryId(@PathVariable(name = "id") String categoryName) {
        List<ProductResponse> responses = productService.getByCategoryId(categoryName);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Product found", responses);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<WebResponse<ProductResponse>> getStoreById(@PathVariable(name = "id") String id) {
        ProductResponse response = productService.getOne(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Product found", response);
    }

    @GetMapping()
    public ResponseEntity<WebResponse<List<ProductResponse>>> search(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "q", required = false) String q
    ) {

        SearchRequest request = SearchRequest.builder()
                .query(q)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        Page<ProductResponse> responses = productService.search(request);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, "Product found", responses);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<WebResponse<ProductResponse>> updateById(@PathVariable(name = "id") String id, @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateById(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Product updated", response);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<WebResponse<String>> deleteProductById(@PathVariable(name = "id") String id) {
        productService.removeById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Product deleted", null);
    }


}
