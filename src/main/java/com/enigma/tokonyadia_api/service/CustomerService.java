package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.RegisterRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {

    CustomerResponse create(RegisterRequest customer);

    Page<CustomerResponse> search(SearchRequest request);

    Customer getById(String id);

    CustomerResponse update(String id, RegisterRequest customer);

    CustomerResponse getOne(String id);

    void removeById(String id);


}
