package com.enigma.tokonyadia_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

    private Integer status;

    private String message;

    private T data;

    private PagingResponse paging;

}
