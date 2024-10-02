package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.entity.Customer;
import com.enigma.tokonyadia_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping()
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @GetMapping(path = "{id}")
    public Customer getCustomerById(@PathVariable(name = "id") String id) {
        return customerService.getById(id);
    }

    @PutMapping("{id}")
    public Customer updateCustomerById(@PathVariable(name = "id") String id, @RequestBody Customer customer) {
        return customerService.updateById(id, customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAll();
    }

    @DeleteMapping(path = "{id}")
    public void deleteCustomerById(@PathVariable(name = "id") String id) {
        customerService.removeById(id);
    }
}
