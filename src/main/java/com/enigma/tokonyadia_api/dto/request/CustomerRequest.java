package com.enigma.tokonyadia_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {


    private String name;

    private String address;

    private String phone;

    private String email;
}
