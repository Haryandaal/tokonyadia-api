package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.dto.request.AuthRequest;
import com.enigma.tokonyadia_api.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);
    AuthResponse refreshToken(String request);
    void logout(String accessToken);
}
