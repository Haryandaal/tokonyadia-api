package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.constant.PaymentStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private String cartId;
    private Long amount;
    private PaymentStatus paymentStatus;
    private String tokenSnap;
    private String redirectUrl;
}
