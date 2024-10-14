package com.enigma.tokonyadia_api.service.Impl;

import com.enigma.tokonyadia_api.dto.request.AuthRequest;
import com.enigma.tokonyadia_api.dto.response.LoginResponse;
import com.enigma.tokonyadia_api.entity.UserAccount;
import com.enigma.tokonyadia_api.service.AuthService;
import com.enigma.tokonyadia_api.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userAccount);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .role(userAccount.getRole().getDescription())
                .build();
    }
}
