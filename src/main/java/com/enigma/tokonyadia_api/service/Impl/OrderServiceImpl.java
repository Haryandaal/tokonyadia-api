package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import com.enigma.tokonyadia_api.dto.request.SearchOrderRequest;
import com.enigma.tokonyadia_api.dto.request.OrderDetailRequest;
import com.enigma.tokonyadia_api.dto.request.OrderRequest;
import com.enigma.tokonyadia_api.dto.request.UpdateOrderStatusRequest;
import com.enigma.tokonyadia_api.dto.response.OrderDetailResponse;
import com.enigma.tokonyadia_api.dto.response.OrderResponse;
import com.enigma.tokonyadia_api.entity.*;
import com.enigma.tokonyadia_api.repository.TransactionRepository;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.enigma.tokonyadia_api.service.ProductService;
import com.enigma.tokonyadia_api.service.OrderService;
import com.enigma.tokonyadia_api.util.DateUtil;
import com.enigma.tokonyadia_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse createDraft(OrderRequest request) {
        validationUtil.validate(request);
        Customer customer = customerService.getById(request.getCustomerId());
        Order draftOrder = Order.builder()
                .customer(customer)
                .orderStatus(OrderStatus.DRAFT)
                .orderDetails(new ArrayList<>())
                .build();
        transactionRepository.saveAndFlush(draftOrder);
        return toTransactionResponse(draftOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDetailResponse> getOrderDetails(String orderId) {
        Order order = getById(orderId);
        return order.getOrderDetails().stream()
                .map(this::toTransactionDetailResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse addOrderDetail(String orderId, OrderDetailRequest request) {
        validationUtil.validate(request);
        Order order = getById(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only add items to draft orders");

        Product product = productService.getById(request.getProductId());

        Optional<OrderDetail> existingOrderDetail = order.getOrderDetails().stream()
                .filter(orderDetail -> orderDetail.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingOrderDetail.isPresent()) {
            OrderDetail orderDetail = existingOrderDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + request.getQuantity());
            orderDetail.setPrice(product.getPrice());
        } else {
            OrderDetail newOrderDetail = OrderDetail.builder()
                    .product(product)
                    .order(order)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .build();
            order.getOrderDetails().add(newOrderDetail);
        }
        transactionRepository.save(order);
        return toTransactionResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse updateOrderDetail(String orderId, String detailId, OrderDetailRequest request) {
        validationUtil.validate(request);
        Order order = getById(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only update items to draft orders");

        OrderDetail orderDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getId().equals(detailId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "order details not found"));

        Product product = productService.getById(request.getProductId());
        orderDetail.setProduct(product);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPrice(product.getPrice());

        transactionRepository.save(order);
        return toTransactionResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse removeOrderDetails(String orderId, String detailId) {
        Order order = getById(orderId);
        if (order.getOrderStatus() != OrderStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete items to draft orders");

        order.getOrderDetails().removeIf(detail -> detail.getId().equals(detailId));
        transactionRepository.save(order);
        return toTransactionResponse(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        validationUtil.validate(request);
        Order order = getById(orderId);
        order.setOrderStatus(request.getStatus());
        transactionRepository.save(order);
        return toTransactionResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> getAllOrders(SearchOrderRequest request) {
//        Sort sortBy = SortUtil.parseSort(request.getSort());
////        Specification<Store> specification = StoreSpecification.getSpecification(request.getQuery());
//        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), sortBy);
//        return transactionRepository.findAll(specification, pageRequest).map(this::toTransactionResponse);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOne(String id) {
        Order order = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
        return toTransactionResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Order getById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));
    }

    private OrderResponse toTransactionResponse(Order order) {
        List<OrderDetailResponse> transactionDetailResponses = order.getOrderDetails().stream()
                .map(this::toTransactionDetailResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .orderDate(DateUtil.localDateTimeToString(order.getOrderDate()))
                .orderStatus(order.getOrderStatus())
                .orderDetails(transactionDetailResponses)
                .build();
    }

    private OrderDetailResponse toTransactionDetailResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .productId(orderDetail.getProduct().getId())
                .productName(orderDetail.getProduct().getName())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .build();
    }
}
