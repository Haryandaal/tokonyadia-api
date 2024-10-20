package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    private String id;

    private String customerId;

    private String customerName;

    private List<CartItemResponse> cartItems;

    private String orderDate;

    private OrderStatus orderStatus;

}
