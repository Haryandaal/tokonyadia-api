package com.enigma.tokonyadia_api.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransItemDetailRequest {

    private String name;

    private Long price;

    private Integer quantity;

    private String category;

}
