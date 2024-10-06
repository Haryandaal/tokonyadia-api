package com.enigma.tokonyadia_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {

    private String id;

    private String noSiup;

    private String name;

    private String phone;

    private String address;

    private List<ProductInStoreResponse> products;
}
