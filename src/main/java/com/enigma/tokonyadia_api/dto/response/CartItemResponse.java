package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.entity.Cart;
import com.enigma.tokonyadia_api.entity.Product;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {

    private String id;

    private String productId;
    private String productName;

    private Integer quantity;

    private Long price;
}
