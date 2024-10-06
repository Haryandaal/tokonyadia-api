package com.enigma.tokonyadia_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInStoreResponse {

    private String id;

    private String name;

    private String description;

    private Long price;

    private Integer stock;

}
