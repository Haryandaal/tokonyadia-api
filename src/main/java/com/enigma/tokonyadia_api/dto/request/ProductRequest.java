package com.enigma.tokonyadia_api.dto.request;

import com.enigma.tokonyadia_api.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String name;

    private String description;

    private Long price;

    private Integer stock;

    private Store store;
}
