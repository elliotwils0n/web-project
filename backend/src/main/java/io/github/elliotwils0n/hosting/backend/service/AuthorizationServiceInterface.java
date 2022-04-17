package io.github.elliotwils0n.hosting.backend.service;

import io.github.elliotwils0n.hosting.backend.infrastructure.ServerGenericException;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface AuthorizationServiceInterface {

    TokenPair refreshToken(String refreshToken);

    TokenPair generateToken(Credentials credentials);

    boolean isAccessTokenValid(String accessToken);

    class InvalidCredentialsException extends ServerGenericException {
        public InvalidCredentialsException() {
            super("Authorization failed. Invalid Credentials provided.");
        }
    }

    class InvalidRefreshedTokenProvided extends ServerGenericException {
        public InvalidRefreshedTokenProvided() {
            super("Refreshing access token failed. Invalid refreshed token provided.");
        }
    }

}
