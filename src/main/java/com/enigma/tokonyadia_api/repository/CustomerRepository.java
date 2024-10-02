package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

}
