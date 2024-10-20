package com.enigma.tokonyadia_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(value = "no_siup")
    private String noSiup;

    private String name;

    private String phone;

    private String address;

    @JsonProperty(value = "store_admin_id")
    private String storeAdminId;
}
