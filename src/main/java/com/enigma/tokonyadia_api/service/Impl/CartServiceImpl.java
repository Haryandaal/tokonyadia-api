package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.CartItemResponse;
import com.enigma.tokonyadia_api.dto.response.CartResponse;
import com.enigma.tokonyadia_api.entity.*;
import com.enigma.tokonyadia_api.repository.CartRepository;
import com.enigma.tokonyadia_api.service.CartService;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.util.DateUtil;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse addProductToCart(OrderDetailRequest request) {
        validationUtil.validate(request);
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Product product = productService.getById(request.getProductId());

        Cart cart = cartRepository.findByCustomerAndOrderStatus(customer, OrderStatus.DRAFT)
                .orElseGet(() -> createNewCart(customer));

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cartRepository.saveAndFlush(cart);
        return toCartResponse(cart);
    }

    private Cart createNewCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setOrderStatus(OrderStatus.DRAFT);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }

    @Override
    public Cart getById(String id) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        return cartRepository.findByCustomerAndOrderStatus(customer, OrderStatus.DRAFT)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    @Override
    public CartResponse getCart() {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Cart cart = cartRepository.findByCustomerAndOrderStatus(customer, OrderStatus.DRAFT)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        return toCartResponse(cart);
    }

    @Override
    public List<CartResponse> getAllCarts() {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartItemResponse> getOrderDetails(String cartId) {
        Cart order = getById(cartId);
        return order.getCartItems().stream()
                .map(this::toTransactionDetailResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse updateOrderDetail(String cartId, String detailId, OrderDetailRequest request) {
        validationUtil.validate(request);
        Cart order = getById(cartId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only update items to draft carts");

        CartItem orderDetail = order.getCartItems().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "cart items not found"));

        Product product = productService.getById(request.getProductId());
        orderDetail.setProduct(product);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPrice(product.getPrice());

        cartRepository.save(order);
        return toCartResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse removeOrderDetails(String cartId, String detailId) {
        Cart order = getById(cartId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete items to draft carts");

        order.getCartItems().removeIf(detail -> detail.getId().equals(detailId));
        cartRepository.save(order);
        return toCartResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse updateOrderStatus(String cartId, UpdateOrderStatusRequest request) {
        validationUtil.validate(request);
        Cart order = getById(cartId);
        order.setOrderStatus(request.getStatus());
        cartRepository.save(order);
        return toCartResponse(order);
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> transactionDetailResponses = cart.getCartItems().stream()
                .map(this::toTransactionDetailResponse)
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer().getId())
                .customerName(cart.getCustomer().getName())
                .orderStatus(cart.getOrderStatus())
                .orderDate(DateUtil.localDateTimeToString(cart.getOrderDate()))
                .cartItems(transactionDetailResponses)
                .build();
    }

    private CartItemResponse toTransactionDetailResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }
}
