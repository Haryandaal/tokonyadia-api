package com.enigma.tokonyadia_api.dto.response;

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
