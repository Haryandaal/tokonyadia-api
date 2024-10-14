package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.CustomerCreateRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest customer);

    Page<CustomerResponse> search(SearchRequest request);

    CustomerResponse getById(String id);

    CustomerResponse update(String id, CustomerCreateRequest customer);

    void removeById(String id);


}
