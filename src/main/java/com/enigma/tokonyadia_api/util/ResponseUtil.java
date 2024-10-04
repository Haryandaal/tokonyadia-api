package com.enigma.tokonyadia_api.util;

import com.enigma.tokonyadia_api.dto.response.PagingResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseUtil {

    public static <T> ResponseEntity<WebResponse<T>> buildResponse(HttpStatus httpStatus, String message, T data) {
        WebResponse<T> response = new WebResponse<>(httpStatus.value(), message, data, null);
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<WebResponse<List<T>>> buildResponsePage(
            HttpStatus httpStatus,
            String message,
            Page<T> page) {
        PagingResponse pagingResponse = PagingResponse.builder()
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .page(page.getPageable().getPageNumber() + 1)
                .size(page.getSize())
                .build();

        WebResponse<List<T>> response = new WebResponse<>(
                httpStatus.value(),
                message,
                page.getContent(),
                pagingResponse
        );
        return ResponseEntity.status(httpStatus).body(response);
    }
}
