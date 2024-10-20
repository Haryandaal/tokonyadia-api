package com.enigma.tokonyadia_api.service;


import com.enigma.tokonyadia_api.dto.request.SearchOrderRequest;
import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.request.OrderRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.OrderDetailResponse;
import com.enigma.tokonyadia_api.dto.response.OrderResponse;
import com.enigma.tokonyadia_api.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse createDraft(OrderRequest request);
    List<OrderDetailResponse> getOrderDetails(String orderId);
    OrderResponse addOrderDetail(String orderId, OrderDetailRequest request);
    OrderResponse updateOrderDetail(String orderId, String detailId, OrderDetailRequest request);
    OrderResponse removeOrderDetails(String orderId, String detailId);
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    Page<OrderResponse> getAllOrders(SearchOrderRequest request);
    OrderResponse getOne(String id);
    Order getById(String id);
}
