package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.entity.Customer;

import java.util.List;

public interface CustomerService {

    Customer create(Customer customer);

    List<Customer> getAll();

    Customer getById(String id);

    Customer updateById(String id, Customer customer);

    void removeById(String id);


}
