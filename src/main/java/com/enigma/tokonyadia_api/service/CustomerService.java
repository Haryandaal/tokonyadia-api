package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.CustomerRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;

public interface CustomerService {

    CustomerResponse create(CustomerRequest customer);

    Page<CustomerResponse> search(SearchRequest request);

    CustomerResponse getById(String id);

    CustomerResponse updateById(String id, CustomerRequest customer);

    void removeById(String id);


}
