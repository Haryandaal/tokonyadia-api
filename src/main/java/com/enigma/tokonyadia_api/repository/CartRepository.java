package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import com.enigma.tokonyadia_api.entity.Cart;
import com.enigma.tokonyadia_api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByCustomerAndOrderStatus(Customer customer, OrderStatus status);
}
