package io.github.elliotwils0n.hosting.backend.service;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.infrastructure.GenericServerException;
import io.github.elliotwils0n.hosting.backend.model.Credentials;

public interface AccountsServiceInterface {

    AccountDto createAccount(Credentials credentials);

    boolean isPasswordValid(Credentials credentials);

    class AccountDoesNotExist extends GenericServerException {
        public AccountDoesNotExist() {
            super("Account does not exist.");
        }
    }

    class UsernameAlreadyTakenException extends GenericServerException {
        public UsernameAlreadyTakenException() {
            super("Username already taken.");
        }
    }

}
