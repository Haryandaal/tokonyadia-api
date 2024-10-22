package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.CartItemRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.CartItemResponse;
import com.enigma.tokonyadia_api.dto.response.CartResponse;
import com.enigma.tokonyadia_api.entity.Cart;

import java.util.List;

public interface CartService {

    CartResponse addProductToCart(CartItemRequest request);

    Cart getById(String id);

    List<CartResponse> getAllCarts();

    CartResponse getCart();

    List<CartItemResponse> getCartItems(String cartId);

    CartResponse updateCartItem(String cartId, String detailId, CartItemRequest request);


    CartResponse removeCartItems(String cartId, String detailId);

    CartResponse updateProductFromCart(CartItemRequest request);

    CartResponse updateOrderStatus(String cartId, UpdateOrderStatusRequest request);

}
