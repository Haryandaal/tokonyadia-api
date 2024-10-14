package com.enigma.tokonyadia_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreateRequest {

    private String username;

    private String password;

    private String name;

    private String address;

    private String phone;

    private String email;
}
