package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.response.CartResponse;
import com.enigma.tokonyadia_api.service.CartService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addProductToCart(@RequestBody OrderDetailRequest request) {
        CartResponse response = cartService.addProductToCart(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Successfully added product to the cart", response);
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        CartResponse response = cartService.getCart();
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully retrieved cart", response);
    }
}