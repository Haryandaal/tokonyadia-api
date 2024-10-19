package com.enigma.tokonyadia_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtransTransactionRequest {

    @JsonProperty(value = "order_id")
    private String transactionId;

    @JsonProperty(value = "gross_amount")
    private Long grossAmount;
}
