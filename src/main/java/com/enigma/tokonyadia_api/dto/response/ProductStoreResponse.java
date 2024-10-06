package com.enigma.tokonyadia_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductStoreResponse {

    private String id;

    private String noSiup;

    private String name;

    private String phone;

    private String address;
}
