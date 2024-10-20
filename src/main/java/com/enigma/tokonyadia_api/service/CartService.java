package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.CartRequest;
import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.CartItemResponse;
import com.enigma.tokonyadia_api.dto.response.CartResponse;
import com.enigma.tokonyadia_api.entity.Cart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {

    CartResponse addProductToCart(OrderDetailRequest request);

    Cart getById(String id);

    List<CartResponse> getAllCarts();

    CartResponse getCart();

    List<CartItemResponse> getOrderDetails(String cartId);

    CartResponse updateOrderDetail(String cartId, String detailId, OrderDetailRequest request);


    CartResponse removeOrderDetails(String cartId, String detailId);

    CartResponse updateOrderStatus(String cartId, UpdateOrderStatusRequest request);

}
