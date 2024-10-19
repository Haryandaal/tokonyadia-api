package com.enigma.tokonyadia_api.service;

public interface RefreshTokenService {

    String createToken(String userId);
    void deleteRefreshToken(String userId);
    String rotateRefreshToken(String userId);
    String getUserIdByToken(String token);
}
