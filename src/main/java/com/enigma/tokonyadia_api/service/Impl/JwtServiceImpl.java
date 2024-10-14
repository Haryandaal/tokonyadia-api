package com.enigma.tokonyadia_api.service.Impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.enigma.tokonyadia_api.entity.UserAccount;
import com.enigma.tokonyadia_api.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${haryanda.tokonyadia.jwt-secret}")
    private String SECRET_KEY;

    @Value("${haryanda.tokonyadia.jwt-expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${haryanda.tokonyadia.jwt-issuer}")
    private String ISSUER;

    @Override
    public String generateAccessToken(UserAccount userAccount) {
        log.info("Generating access token for user: {}", userAccount.getUsername());
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(EXPIRATION_IN_MINUTES, ChronoUnit.MINUTES))
                    .withSubject(userAccount.getId())
                    .withClaim("role", userAccount.getRole().getDescription())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Error creating JWT token - {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating JWT token");
        }
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Validating token: {}", System.currentTimeMillis());
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("Error validating token - {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUserId(String token) {
        log.info("Extracting token JWT: {}", System.currentTimeMillis());
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Error verifying token - {}", e.getMessage());
            return null;
        }
    }
}
