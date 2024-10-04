package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StoreService {

    StoreResponse create(StoreRequest request);

    Page<StoreResponse> search(SearchRequest request);

    StoreResponse getById(String id);

    StoreResponse updateById(String id, StoreRequest request);

    void removeById(String id);

}
