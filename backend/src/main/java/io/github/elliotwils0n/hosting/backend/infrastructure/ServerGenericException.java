package io.github.elliotwils0n.hosting.backend.infrastructure;

public class ServerGenericException extends RuntimeException {

    public ServerGenericException() { }

    public ServerGenericException(String message) {
        super(message);
    }

}
