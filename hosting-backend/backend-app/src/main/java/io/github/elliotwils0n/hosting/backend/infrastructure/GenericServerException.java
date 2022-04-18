package io.github.elliotwils0n.hosting.backend.infrastructure;

public class GenericServerException extends RuntimeException {

    public GenericServerException() { }

    public GenericServerException(String message) {
        super(message);
    }

}
