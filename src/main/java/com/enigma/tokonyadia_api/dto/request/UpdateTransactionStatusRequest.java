package com.enigma.tokonyadia_api.dto.request;

import com.enigma.tokonyadia_api.constant.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTransactionStatusRequest {

    @NotNull(message = "transaction status is required")
    private TransactionStatus status;
}
