package com.enigma.tokonyadia_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {

    @NotBlank(message = "product id is required")
    private String productId;

    @NotNull(message = "quantity is required")
    private Integer quantity;
}
