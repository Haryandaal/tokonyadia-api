package com.enigma.tokonyadia_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequest {

    private String noSiup;

    private String name;

    private String phone;

    private String address;

//    private List<ProductRequest> products;
}
