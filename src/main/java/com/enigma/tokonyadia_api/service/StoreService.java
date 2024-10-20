package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.ProductResponse;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.entity.Store;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StoreService {

    StoreResponse create(String storeAdminId, StoreRequest request);

    List<ProductInStoreResponse>getByStoreId(String storeId);

    Page<StoreResponse> search(SearchRequest request);

    Store getById(String id);

    StoreResponse getOne(String id);

    StoreResponse updateById(String id, StoreRequest request);

    void removeById(String id);

}
