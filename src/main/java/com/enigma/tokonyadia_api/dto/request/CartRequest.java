package com.enigma.tokonyadia_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {

    private String customerId;

    private String productId;
}
