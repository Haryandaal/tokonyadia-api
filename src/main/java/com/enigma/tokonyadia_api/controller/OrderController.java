package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.request.OrderRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.OrderDetailResponse;
import com.enigma.tokonyadia_api.dto.response.OrderResponse;
import com.enigma.tokonyadia_api.service.OrderService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "draft")
    public ResponseEntity<?> createDraftOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createDraft(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Successfully created draft order", response);
    }

    @GetMapping(path = "{orderId}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") String orderId) {
        List<OrderDetailResponse> orderDetails = orderService.getOrderDetails(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully retrieved order details", orderDetails);
    }

    @PostMapping(path = "{orderId}/details")
    public ResponseEntity<?> addOrderDetails(@PathVariable("orderId") String orderId, @RequestBody OrderDetailRequest request) {
        OrderResponse response = orderService.addOrderDetail(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Successfully added details order", response);
    }

    @PutMapping(path = "{orderId}/details/{detailId}")
    public ResponseEntity<?> updateOrderDetails(@PathVariable("orderId") String orderId, @PathVariable("detailId") String detailId, @RequestBody OrderDetailRequest request) {
        OrderResponse response = orderService.updateOrderDetail(orderId, detailId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully updated details order", response);
    }

    @DeleteMapping("/{orderId}/details/{detailId}")
    public ResponseEntity<?> removeOrderDetail(@PathVariable("orderId") String orderId, @PathVariable("detailId") String detailId) {
        OrderResponse response = orderService.removeOrderDetails(orderId, detailId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully deleted details order", response);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully updated order status", request);
    }

//    @PostMapping("/{transId}/checkout")
//    public ResponseEntity<?> checkoutOrder(@PathVariable("transId") String orderId) {
//        TransactionResponse response = transactionService.checkoutTransaction(orderId);
//        return ResponseUtil.buildResponse(HttpStatus.OK, "Success checkout transaction", response);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success get order by id", orderService.getById(id));
    }
}
