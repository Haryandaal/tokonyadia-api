package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.UserRequest;
import com.enigma.tokonyadia_api.dto.response.UserResponse;
import com.enigma.tokonyadia_api.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserResponse create(UserRequest userRequest);

    UserAccount create(UserAccount userAccount);

    UserAccount getById(String id);

    UserResponse getAuthentication();
}
