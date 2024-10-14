package com.enigma.tokonyadia_api.service;

import com.enigma.tokonyadia_api.entity.UserAccount;

public interface JwtService {

    String generateAccessToken(UserAccount userAccount);

    boolean validateToken(String token);

    String getUserId(String token);
}
