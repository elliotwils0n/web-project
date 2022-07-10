package io.github.elliotwils0n.hosting.backend.integration;

import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.repository.AccountsRepository;
import io.github.elliotwils0n.hosting.backend.service.AccountsServiceInterface;
import io.github.elliotwils0n.hosting.backend.service.implementation.AccountsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class AccountsServiceIT {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Test
    @Rollback
    @Transactional
    public void shouldCreateAccount_whenUsernameNotTaken() {
        // given
        Credentials credentials = new Credentials("john_doe", "super_secret_password");

        // when
        accountsService.createAccount(credentials);

        // then
        Optional<AccountEntity> account = accountsRepository.findByUsername(credentials.getUsername());

        Assertions.assertTrue(account.isPresent());
        Assertions.assertNotNull(account.get().getId());
        Assertions.assertEquals(credentials.getUsername(), account.get().getUsername());
        Assertions.assertEquals(60, account.get().getPasswordHash().length());
        Assertions.assertTrue(accountsService.isPasswordValid(credentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenUsernameTaken() {
        // given
        Credentials credentials = new Credentials("john_doe", "super_secret_password");

        AccountEntity account = new AccountEntity(credentials.getUsername(), "not_important");
        accountsRepository.save(account);

        // when
        // then
        Assertions.assertThrows(AccountsServiceInterface.UsernameAlreadyTakenException.class, () -> accountsService.createAccount(credentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldConfirmCredentials_whenCorrectPasswordGiven() {
        // given
        Credentials credentials = new Credentials("john_doe", "super_secret_password");

        accountsService.createAccount(credentials);

        // when
        boolean validation = accountsService.isPasswordValid(credentials);

        // then
        Assertions.assertTrue(validation);
    }

    @Test
    @Rollback
    @Transactional
    public void shouldNotConfirmCredentials_whenIncorrectPasswordGiven() {
        // given
        Credentials credentials = new Credentials("john_doe", "super_secret_password");
        Credentials invalidCredentials = new Credentials(credentials.getUsername(), "invalid_password");

        accountsService.createAccount(credentials);

        // when
        boolean validation = accountsService.isPasswordValid(invalidCredentials);

        // then
        Assertions.assertFalse(validation);
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenAccountDoesNotExist() {
        // given
        Credentials credentials = new Credentials("john_doe", "super_secret_password");

        // when
        // then
        Assertions.assertThrows(AccountsServiceInterface.AccountDoesNotExist.class, () -> accountsService.isPasswordValid(credentials));
    }
}
