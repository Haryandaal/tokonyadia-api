package com.enigma.tokonyadia_api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum OrderStatus {

    DRAFT("draft"),
    PENDING("Pending"),
    FAILED("failed"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    COMPLETED("Completed");

    private final String description;
}
