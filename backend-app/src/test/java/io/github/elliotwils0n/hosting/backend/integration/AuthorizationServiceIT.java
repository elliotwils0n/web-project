package io.github.elliotwils0n.hosting.backend.integration;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.entity.SessionEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.github.elliotwils0n.hosting.backend.repository.SessionsRepository;
import io.github.elliotwils0n.hosting.backend.service.AccountsServiceInterface;
import io.github.elliotwils0n.hosting.backend.service.AuthorizationServiceInterface;
import io.github.elliotwils0n.hosting.backend.service.implementation.AccountsService;
import io.github.elliotwils0n.hosting.backend.service.implementation.AuthorizationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class AuthorizationServiceIT {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private SessionsRepository sessionsRepository;

    @Autowired
    private AccountsService accountsService;

    @Value("${files.root.directory}")
    private String value;

    @Test
    @Rollback
    @Transactional
    public void shouldReturnTokenPair_whenValidCredentialsProvided() {

        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        AccountDto account = accountsService.createAccount(credentials);

        // when
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);
        Optional<SessionEntity> sessionEntity = sessionsRepository.findFirstByAccountIdAndActiveOrderByModificationTimeDesc(account.getId(), true);

        // then
        Assertions.assertNotNull(tokenPair.getAccessToken());
        Assertions.assertNotNull(tokenPair.getRefreshToken());
        Assertions.assertTrue(sessionEntity.isPresent());
        Assertions.assertEquals(tokenPair.getAccessToken(), sessionEntity.get().getAccessToken());
        Assertions.assertEquals(tokenPair.getRefreshToken(), sessionEntity.get().getRefreshToken());
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenInvalidCredentialsProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");
        Credentials invalidCredentials = new Credentials("john_doe", "invalid_raw_password");

        accountsService.createAccount(credentials);

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.InvalidCredentialsException.class, () -> authorizationService.generateTokenPair(invalidCredentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenAccountDoesNotExist() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        // when
        // then
        Assertions.assertThrows(AccountsServiceInterface.AccountDoesNotExist.class, () -> authorizationService.generateTokenPair(credentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnTokenPair_whenValidRefreshTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        AccountDto account = accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        // when
        TokenPair refreshedTokenPair = authorizationService.refreshTokens(tokenPair.getRefreshToken());
        Optional<SessionEntity> sessionEntity = sessionsRepository.findFirstByAccountIdAndActiveOrderByModificationTimeDesc(account.getId(), true);

        // then
        Assertions.assertNotNull(refreshedTokenPair.getAccessToken());
        Assertions.assertNotNull(refreshedTokenPair.getRefreshToken());
        Assertions.assertTrue(sessionEntity.isPresent());
        Assertions.assertEquals(refreshedTokenPair.getAccessToken(), sessionEntity.get().getAccessToken());
        Assertions.assertEquals(refreshedTokenPair.getRefreshToken(), sessionEntity.get().getRefreshToken());
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenInvalidRefreshTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.SessionExpiredException.class,() -> authorizationService.refreshTokens(tokenPair.getAccessToken()));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenRefreshTokenNotExisting() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        sessionsRepository.deleteAllInBatch();

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.SessionNotFoundException.class,() -> authorizationService.refreshTokens(tokenPair.getAccessToken()));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnTrue_whenValidAccessTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        // when
        // then
        Assertions.assertDoesNotThrow(() -> authorizationService.validateAccessToken(tokenPair.getAccessToken()));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnFalse_whenInvalidAccessTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateTokenPair(credentials);

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.SessionExpiredException.class, () -> authorizationService.validateAccessToken(tokenPair.getRefreshToken()));
    }

}
