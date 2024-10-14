package com.enigma.tokonyadia_api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerResponse {

    private String id;

    private String name;

    private String address;

    private String phone;

    private String email;

    private String userId;
}
