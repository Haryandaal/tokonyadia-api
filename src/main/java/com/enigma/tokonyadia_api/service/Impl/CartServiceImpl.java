package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import com.enigma.tokonyadia_api.dto.request.CartItemRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.CartItemResponse;
import com.enigma.tokonyadia_api.dto.response.CartResponse;
import com.enigma.tokonyadia_api.entity.*;
import com.enigma.tokonyadia_api.repository.CartItemRepository;
import com.enigma.tokonyadia_api.repository.CartRepository;
import com.enigma.tokonyadia_api.service.CartService;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.util.DateUtil;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse addProductToCart(CartItemRequest request) {
        log.info("Start adding product to the cart, {}", System.currentTimeMillis());
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
            int updatedQuantity = cartItem.getQuantity() + request.getQuantity();
            if (product.getStock() < updatedQuantity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + product.getName() + " is out of stock.");
            }
            cartItem.setQuantity(updatedQuantity);
            if (cartItem.getQuantity() <= 0) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            }
        } else {
            if (request.getQuantity() > 0) {
                CartItem cartItem = CartItem.builder()
                        .product(product)
                        .cart(cart)
                        .quantity(request.getQuantity())
                        .price(product.getPrice())
                        .build();
                cart.getCartItems().add(cartItem);
            }
        }

        if (cart.getCartItems().isEmpty()) {
            cartRepository.delete(cart);
        }

        cartRepository.saveAndFlush(cart);
        log.info("End adding product to the cart, {}", System.currentTimeMillis());
        return toCartResponse(cart);
    }


    private Cart createNewCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setOrderStatus(OrderStatus.DRAFT);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }

    @Transactional(readOnly = true)
    @Override
    public Cart getById(String id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart() {
        log.info("Start getting cart.");
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
    public List<CartItemResponse> getCartItems(String cartId) {
        Cart order = getById(cartId);
        return order.getCartItems().stream()
                .map(this::toTransactionDetailResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CartResponse updateCartItem(String cartId, String detailId, CartItemRequest request) {
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
    public CartResponse removeCartItems(String cartId, String detailId) {
        Cart order = getById(cartId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete items to draft carts");

        order.getCartItems().removeIf(detail -> detail.getId().equals(detailId));
        cartRepository.save(order);
        return toCartResponse(order);
    }

    @Override
    public CartResponse updateProductFromCart(CartItemRequest request) {
        UserAccount userAccount = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = userAccount.getCustomer();
        Product product = productService.getById(request.getProductId());

        Cart cart = cartRepository.findByCustomerAndOrderStatus(customer, OrderStatus.DRAFT)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItem cartItem = cart.getCartItems().stream().filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // set quantity nya, jika quantity 0, cart item otomatis hapus
        cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());
        if (cartItem.getQuantity() <= 0) {
            cart.getCartItems().remove(cartItem);
            cartRepository.delete(cart);
        }

        if (cart.getCartItems().isEmpty()) {
            cartRepository.delete(cart);
            return null;
        }
        cartRepository.save(cart);
        return toCartResponse(cart);
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
