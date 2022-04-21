package io.github.elliotwils0n.hosting.backend.integration;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.entity.AccessTokenEntity;
import io.github.elliotwils0n.hosting.backend.entity.RefreshTokenEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.github.elliotwils0n.hosting.backend.repository.AccessTokensRepository;
import io.github.elliotwils0n.hosting.backend.repository.RefreshTokensRepository;
import io.github.elliotwils0n.hosting.backend.service.AccountsServiceInterface;
import io.github.elliotwils0n.hosting.backend.service.AuthorizationServiceInterface;
import io.github.elliotwils0n.hosting.backend.service.implementation.AccountsService;
import io.github.elliotwils0n.hosting.backend.service.implementation.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
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
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

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
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        // then
        Assertions.assertNotNull(tokenPair.getAccessToken());
        Assertions.assertNotNull(tokenPair.getRefreshToken());

        Optional<AccessTokenEntity> accessToken = accessTokensRepository.findByAccountId(account.getId());
        Optional<RefreshTokenEntity> refreshToken = refreshTokensRepository.findByAccountId(account.getId());

        Assertions.assertTrue(accessToken.isPresent());
        Assertions.assertEquals(tokenPair.getAccessToken(), accessToken.get().getAccessToken());
        Assertions.assertTrue(refreshToken.isPresent());
        Assertions.assertEquals(tokenPair.getRefreshToken(), refreshToken.get().getRefreshToken());
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
        Assertions.assertThrows(AuthorizationServiceInterface.InvalidCredentialsException.class, () -> authorizationService.generateToken(invalidCredentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenAccountDoesNotExist() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        // when
        // then
        Assertions.assertThrows(AccountsServiceInterface.AccountDoesNotExist.class, () -> authorizationService.generateToken(credentials));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnTokenPair_whenValidRefreshTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        AccountDto account = accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        // when
        TokenPair refreshedTokenPair = authorizationService.refreshToken(tokenPair.getRefreshToken());

        // then
        Assertions.assertNotNull(refreshedTokenPair.getAccessToken());
        Assertions.assertNotNull(refreshedTokenPair.getRefreshToken());

        Optional<AccessTokenEntity> accessToken = accessTokensRepository.findByAccountId(account.getId());
        Optional<RefreshTokenEntity> refreshToken = refreshTokensRepository.findByAccountId(account.getId());

        Assertions.assertTrue(accessToken.isPresent());
        Assertions.assertEquals(refreshedTokenPair.getAccessToken(), accessToken.get().getAccessToken());
        Assertions.assertTrue(refreshToken.isPresent());
        Assertions.assertEquals(refreshedTokenPair.getRefreshToken(), refreshToken.get().getRefreshToken());
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenInvalidRefreshTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.InvalidRefreshedTokenProvided.class,() -> authorizationService.refreshToken(tokenPair.getAccessToken()));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldThrowException_whenRefreshTokenNotExisting() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        refreshTokensRepository.deleteAllInBatch();

        // when
        // then
        Assertions.assertThrows(AuthorizationServiceInterface.InvalidRefreshedTokenProvided.class,() -> authorizationService.refreshToken(tokenPair.getAccessToken()));
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnTrue_whenValidAccessTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        // when
        boolean validation = authorizationService.isAccessTokenValid(tokenPair.getAccessToken());

        // then
        Assertions.assertTrue(validation);
    }

    @Test
    @Rollback
    @Transactional
    public void shouldReturnFalse_whenInvalidAccessTokenProvided() {
        // given
        Credentials credentials = new Credentials("john_doe", "raw_password");

        accountsService.createAccount(credentials);
        TokenPair tokenPair = authorizationService.generateToken(credentials);

        // when
        boolean validation = authorizationService.isAccessTokenValid(tokenPair.getRefreshToken());

        // then
        Assertions.assertFalse(validation);
    }

}
