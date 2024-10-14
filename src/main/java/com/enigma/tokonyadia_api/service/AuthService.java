package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.AuthRequest;
import com.enigma.tokonyadia_api.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(AuthRequest request);

}
