package com.enigma.tokonyadia_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransPaymentRequest {

    @JsonProperty(value = "transaction_details")
    private MidtransTransactionRequest transactionDetails;

    @JsonProperty(value = "enabled_payments")
    private List<String> enabledPayment;
}
