package com.enigma.tokonyadia_api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Long price;
}
