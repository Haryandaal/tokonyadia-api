package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.ProductRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    Page<ProductResponse> search(SearchRequest request);

    ProductResponse getById(String id);

    ProductResponse updateById(String id, ProductRequest request);

    void removeById(String id);
}
