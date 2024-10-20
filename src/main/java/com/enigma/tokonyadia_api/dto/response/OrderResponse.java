package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String id;
    private String customerId;
    private String customerName;
    private String orderDate;
    private OrderStatus orderStatus;
    private List<OrderDetailResponse> orderDetails;
}
