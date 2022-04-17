package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.entity.AccessTokenEntity;
import io.github.elliotwils0n.hosting.backend.entity.RefreshTokenEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.github.elliotwils0n.hosting.backend.repository.AccessTokensRepository;
import io.github.elliotwils0n.hosting.backend.repository.RefreshTokensRepository;
import io.github.elliotwils0n.hosting.backend.service.AuthorizationServiceInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthorizationService implements AuthorizationServiceInterface {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccessTokensRepository accessTokensRepository;

    @Autowired
    private RefreshTokensRepository refreshTokensRepository;

    @Value("${authorization.token.expiration.time}")
    private Long tokenExpirationTime;

    @Value("${authorization.token.signing.secret}")
    private String tokenSigningSecret;

    @Override
    public TokenPair refreshToken(String refreshToken) {

        Jws<Claims> claims = getParsedRefreshToken(refreshToken);
        UUID accountId = UUID.fromString(claims.getBody().get("account_id").toString());

        LocalDateTime now = LocalDateTime.now();
        String newAccessToken = getNewAccessToken(accountId, now);
        String newRefreshToken = getNewRefreshToken(accountId, now);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Override
    public TokenPair generateToken(Credentials credentials) {
        boolean validation = accountsService.isPasswordValid(credentials);

        if (!validation) {
            throw new InvalidCredentialsException();
        }

        AccountDto account = accountsService.findByUsername(credentials.getUsername());

        LocalDateTime now = LocalDateTime.now();
        String newAccessToken = getNewAccessToken(account.getId(), now);
        String newRefreshToken = getNewRefreshToken(account.getId(), now);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        Jws<Claims> claims = parseJwtToken(accessToken);

        if (!claims.getBody().getSubject().equals("access_token")) {
            return false;
        }

        UUID accountId = UUID.fromString(claims.getBody().get("account_id").toString());
        LocalDateTime expiration = claims.getBody().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Optional<AccessTokenEntity> accessTokenEntity = accessTokensRepository.findByAccountId(accountId);

        return  accessTokenEntity.isPresent() && accessToken.equals(accessTokenEntity.get().getAccessToken()) && expiration.isAfter(LocalDateTime.now());
    }

    public Optional<UUID> getAccountIdFromAccessToken(String accessToken) {
        return Optional.of(UUID.fromString(parseJwtToken(accessToken).getBody().get("account_id").toString()));
    }

    @Transactional
    private String getNewAccessToken(UUID accountId, LocalDateTime time) {
        String refreshToken = prepareAccessToken(accountId, time);

        Optional<AccessTokenEntity> accessTokenEntity = accessTokensRepository.findByAccountId(accountId);

        accessTokenEntity.ifPresentOrElse(
                entity -> {
                    entity.setAccessToken(refreshToken);
                    entity.setGeneratedAt(time);
                    entity.setValidTo(time.plusMinutes(tokenExpirationTime));
                    accessTokensRepository.save(entity);
                },
                () -> {
                    AccessTokenEntity entity = new AccessTokenEntity();
                    entity.setAccountId(accountId);
                    entity.setAccessToken(refreshToken);
                    entity.setGeneratedAt(time);
                    entity.setValidTo(time.plusMinutes(tokenExpirationTime));
                    accessTokensRepository.save(entity);
                });

        return refreshToken;
    }

    @Transactional
    private String getNewRefreshToken(UUID accountId, LocalDateTime time) {
        String refreshToken = prepareRefreshToken(accountId, time);

        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokensRepository.findByAccountId(accountId);

        refreshTokenEntity.ifPresentOrElse(
                entity -> {
                    entity.setRefreshToken(refreshToken);
                    entity.setGeneratedAt(time);
                    entity.setUsed(false);
                    refreshTokensRepository.save(entity);
                },
                () -> {
                    RefreshTokenEntity entity = new RefreshTokenEntity();
                    entity.setAccountId(accountId);
                    entity.setRefreshToken(refreshToken);
                    entity.setGeneratedAt(time);
                    entity.setUsed(false);
                    refreshTokensRepository.save(entity);
                });

        return refreshToken;
    }

    private String prepareAccessToken(UUID accountId, LocalDateTime time) {
        Date issuedAt = Date.from(Timestamp.valueOf(time).toInstant());
        Date expiration = Date.from(Timestamp.valueOf(time.plusMinutes(tokenExpirationTime)).toInstant());

        return Jwts.builder()
                .claim("account_id", accountId)
                .setSubject("access_token")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getTokenSigningKey())
                .compact();
    }

    private String prepareRefreshToken(UUID accountId, LocalDateTime time) {
        Date issuedAt = Date.from(Timestamp.valueOf(time).toInstant());

        return Jwts.builder()
                .claim("account_id", accountId)
                .setSubject("refresh_token")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(issuedAt)
                .signWith(getTokenSigningKey())
                .compact();
    }

    private Jws<Claims> parseJwtToken(String jwtString) {
        return Jwts.parserBuilder()
                .setSigningKey(getTokenSigningKey())
                .build()
                .parseClaimsJws(jwtString);

    }

    private Jws<Claims> getParsedRefreshToken(String refreshToken) {
        Jws<Claims> claims = parseJwtToken(refreshToken);
        UUID accountId = UUID.fromString(claims.getBody().get("account_id").toString());

        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokensRepository.findByAccountId(accountId);

        if(!claims.getBody().getSubject().equals("refresh_token") || refreshTokenEntity.isEmpty() || !refreshTokenEntity.get().getRefreshToken().equals(refreshToken)) {
            throw new InvalidRefreshedTokenProvided();
        }

        return claims;
    }

    private SecretKeySpec getTokenSigningKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(tokenSigningSecret), SignatureAlgorithm.HS256.getJcaName());
    }
}
