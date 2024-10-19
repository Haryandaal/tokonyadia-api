package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.constant.TransactionStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String id;
    private String customerId;
    private String customerName;
    private String transactionDate;
    private TransactionStatus transactionStatus;
    private List<TransactionDetailResponse> transactionDetails;
}
