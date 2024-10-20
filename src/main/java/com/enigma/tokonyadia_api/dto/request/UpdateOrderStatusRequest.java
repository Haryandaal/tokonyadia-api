package com.enigma.tokonyadia_api.dto.request;

import com.enigma.tokonyadia_api.constant.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "order status is required")
    private OrderStatus status;
}
