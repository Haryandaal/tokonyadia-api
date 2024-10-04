package com.enigma.tokonyadia_api.dto.response;

import com.enigma.tokonyadia_api.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

//    private Product product;
}
