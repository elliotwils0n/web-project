package io.github.elliotwils0n.hosting.backend.service;

import io.github.elliotwils0n.hosting.backend.infrastructure.GenericServerException;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;

public interface AuthorizationServiceInterface {

    TokenPair refreshToken(String refreshToken);

    TokenPair generateToken(Credentials credentials);

    boolean isAccessTokenValid(String accessToken);

    class InvalidCredentialsException extends GenericServerException {
        public InvalidCredentialsException() {
            super("Authorization failed. Invalid Credentials provided.");
        }
    }

    class InvalidRefreshedTokenProvided extends GenericServerException {
        public InvalidRefreshedTokenProvided() {
            super("Refreshing access token failed. Invalid refreshed token provided.");
        }
    }

}
