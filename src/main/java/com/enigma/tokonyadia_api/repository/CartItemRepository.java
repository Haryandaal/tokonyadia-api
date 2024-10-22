package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
}
