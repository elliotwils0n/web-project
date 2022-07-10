package io.github.elliotwils0n.hosting.backend.service;

import io.github.elliotwils0n.hosting.backend.infrastructure.GenericServerException;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;

public interface AuthorizationServiceInterface {

    TokenPair refreshTokens(String refreshToken);

    TokenPair generateTokenPair(Credentials credentials);

    void validateAccessToken(String accessToken);

    class InvalidCredentialsException extends GenericServerException {
        public InvalidCredentialsException() {
            super("Authorization failed. Invalid Credentials provided.");
        }
    }

    class SessionNotFoundException extends GenericServerException {
        public SessionNotFoundException() {
            super("Session not found or expired.");
        }
    }

    class SessionExpiredException extends GenericServerException {
        public SessionExpiredException() {
            super("Session expired.");
        }
    }

}
