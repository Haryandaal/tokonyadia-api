package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.ProductRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import com.enigma.tokonyadia_api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(List<MultipartFile> multipartFiles, ProductRequest request);

    Page<ProductResponse> search(SearchRequest request);

    Product getById(String id);

    List<ProductResponse> getByCategoryId(String categoryId);

    ProductResponse getOne(String id);

    ProductResponse updateById(String id, ProductRequest request);

    @Transactional(rollbackFor = Exception.class)
    void reduceProductStock(Product product, Integer quantity);

    void removeById(String id);
}
