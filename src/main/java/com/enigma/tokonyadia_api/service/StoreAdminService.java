package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.RegisterStoreAdminRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.StoreAdminResponse;
import com.enigma.tokonyadia_api.entity.StoreAdmin;
import org.springframework.data.domain.Page;

public interface StoreAdminService {

    StoreAdminResponse create(RegisterStoreAdminRequest request);
    Page<StoreAdminResponse> search(SearchRequest request);
    StoreAdmin getById(String id);
    StoreAdminResponse update(String id, RegisterStoreAdminRequest request);
    StoreAdminResponse getOne(String id);
    void removeById(String id);
}
