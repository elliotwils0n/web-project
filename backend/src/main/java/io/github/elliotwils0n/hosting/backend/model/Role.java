package io.github.elliotwils0n.hosting.backend.model;

public enum Role {

    AUTHORIZED("ROLE_AUTHORIZED");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
