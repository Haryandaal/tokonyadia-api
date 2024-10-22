package com.enigma.tokonyadia_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransCustomerDetailRequest {

    @JsonProperty(value = "first_name")
    private String name;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "phone")
    private String phone;
}
