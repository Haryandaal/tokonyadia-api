package com.enigma.tokonyadia_api.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    DRAFT("Draft"),
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    COMPLETED("Completed");

    private final String description;
}
