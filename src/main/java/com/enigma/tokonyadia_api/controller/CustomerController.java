package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.CustomerRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity<WebResponse<CustomerResponse>> createCustomer(@RequestBody CustomerRequest customer) {
        CustomerResponse customerResponse = customerService.create(customer);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Customer created", customerResponse);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> getCustomerById(@PathVariable(name = "id") String id) {
        CustomerResponse response = customerService.getById(id);
       return ResponseUtil.buildResponse(HttpStatus.OK, "Customer found", response);
    }

    @PutMapping("{id}")
    public ResponseEntity<WebResponse<CustomerResponse>> updateCustomerById(@PathVariable(name = "id") String id, @RequestBody CustomerRequest customer) {
        CustomerResponse customerResponse = customerService.updateById(id, customer);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Customer found", customerResponse);
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<CustomerResponse>>> getAllCustomers(
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

        Page<CustomerResponse> responses = customerService.search(request);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, "Customer found", responses);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<WebResponse<String>> deleteCustomerById(@PathVariable(name = "id") String id) {
        customerService.removeById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Customer deleted", null);
    }
}
