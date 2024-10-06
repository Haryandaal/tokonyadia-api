package com.enigma.tokonyadia_api.dto.response;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingResponse {

    private Long totalItems;
    private Integer totalPages;
    private Integer page;
    private Integer size;
}
