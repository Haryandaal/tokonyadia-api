package com.enigma.tokonyadia_api.dto.request;

import com.enigma.tokonyadia_api.entity.CartItem;
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

    @JsonProperty(value = "item_details")
    private List<MidtransItemDetailRequest> itemDetails;

    @JsonProperty(value = "customer_details")
    private MidtransCustomerDetailRequest customerDetail;
}
