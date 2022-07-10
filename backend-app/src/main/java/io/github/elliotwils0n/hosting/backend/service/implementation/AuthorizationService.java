package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.entity.SessionEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.model.TokenPair;
import io.github.elliotwils0n.hosting.backend.model.TokenPairProjection;
import io.github.elliotwils0n.hosting.backend.repository.SessionsRepository;
import io.github.elliotwils0n.hosting.backend.service.AuthorizationServiceInterface;
import io.jsonwebtoken.Claims;
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
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthorizationService implements AuthorizationServiceInterface {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private SessionsRepository sessionsRepository;

    @Value("${authorization.access.token.expiration}")
    private Long accessTokenExpirationTime;

    @Value("${authorization.refresh.token.expiration}")
    private Long refreshTokenExpirationTime;

    @Value("${authorization.token.signing.secret}")
    private String tokenSigningSecret;


    @Override
    public TokenPair generateTokenPair(Credentials credentials) {
        if (!accountsService.isPasswordValid(credentials)) {
            throw new InvalidCredentialsException();
        }

        AccountDto account = accountsService.findByUsername(credentials.getUsername());
        killOtherSessions(account.getId());

        return getNewTokenPair(account.getId(), LocalDateTime.now());
    }

    @Transactional
    private void killOtherSessions(UUID accountId) {
        log.info("Killing previous active sessions for account {}", accountId);
        List<SessionEntity> existingSessions = sessionsRepository.findAllByAccountIdAndActive(accountId, true);
        existingSessions.forEach(session -> {
            session.setActive(false);
        });
        sessionsRepository.saveAll(existingSessions);
    }

    @Override
    public TokenPair refreshTokens(String refreshToken) {

        Jws<Claims> claims = getParsedRefreshToken(refreshToken);
        UUID accountId = UUID.fromString(claims.getBody().get("account_id").toString());
        LocalDateTime now = LocalDateTime.now();

        return getNewTokenPair(accountId, now);
    }

    @Override
    public void validateAccessToken(String accessToken) {
        Jws<Claims> claims = parseJwtToken(accessToken);

        UUID sessionId = UUID.fromString(claims.getBody().getId());
        LocalDateTime expiration = claims.getBody().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Optional<SessionEntity> sessionEntity = sessionsRepository.findByIdAndActive(sessionId, true);
        log.info("Validating access token {}...", sessionId);

        if(!claims.getBody().getSubject().equals("access_token") || sessionEntity.isEmpty() || !sessionEntity.get().isActive() || !accessToken.equals(sessionEntity.get().getAccessToken()) || expiration.isBefore(LocalDateTime.now())) {
            log.info("Access token not validated. ");
            throw new SessionExpiredException();
        }
        log.info("Access token validated successfully. ");
    }

    public Optional<UUID> getAccountIdFromAccessToken(String accessToken) {
        return Optional.of(UUID.fromString(parseJwtToken(accessToken).getBody().get("account_id").toString()));
    }

    @Transactional
    private TokenPair getNewTokenPair(UUID accountId, LocalDateTime time) {
        Optional<SessionEntity> sessionEntity = sessionsRepository.findFirstByAccountIdAndActiveOrderByModificationTimeDesc(accountId, true);

        sessionEntity.ifPresentOrElse(
                entity -> {
                    log.info("Token pair exist. Refreshing tokens for account {}.", accountId);
                    String newAccessToken = prepareAccessToken(entity.getId(), entity.getAccountId(), time);
                    String newRefreshToken = prepareRefreshToken(entity.getId(), entity.getAccountId(), time);

                    entity.setAccessToken(newAccessToken);
                    entity.setRefreshToken(newRefreshToken);
                    entity.setAccessTokenExpiration(time.plusMinutes(accessTokenExpirationTime));
                    entity.setRefreshTokenExpiration(time.plusMinutes(refreshTokenExpirationTime));
                    entity.setModificationTime(LocalDateTime.now());
                    sessionsRepository.save(entity);
                    log.info("Token pair refreshed for account {}.", accountId);
                },
                () -> {
                    log.info("Token pair does not exist. Generating tokens for account {}.", accountId);
                    UUID sessionID = UUID.randomUUID();
                    String newAccessToken = prepareAccessToken(sessionID, accountId, time);
                    String newRefreshToken = prepareRefreshToken(sessionID, accountId, time);

                    SessionEntity entity = new SessionEntity();
                    entity.setId(sessionID);
                    entity.setAccountId(accountId);
                    entity.setAccessToken(newAccessToken);
                    entity.setRefreshToken(newRefreshToken);
                    entity.setAccessTokenExpiration(time.plusMinutes(accessTokenExpirationTime));
                    entity.setRefreshTokenExpiration(time.plusMinutes(refreshTokenExpirationTime));
                    entity.setActive(true);
                    entity.setModificationTime(LocalDateTime.now());
                    sessionsRepository.save(entity);
                    log.info("Token pair generated for account {}.", accountId);
                });

        TokenPairProjection tokenPair = sessionsRepository
                .findAccessTokenAndRefreshTokenByAccountIdAndActive(accountId, true)
                .orElseThrow(SessionNotFoundException::new);

        return new TokenPair(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
    }

    private String prepareAccessToken(UUID sessionId, UUID accountId, LocalDateTime time) {
        log.info("Preparing access token for session {} and account {}.", sessionId, accountId);
        Date issuedAt = Date.from(Timestamp.valueOf(time).toInstant());
        Date expiration = Date.from(Timestamp.valueOf(time.plusMinutes(accessTokenExpirationTime)).toInstant());

        return Jwts.builder()
                .setId(sessionId.toString())
                .setSubject("access_token")
                .claim("account_id", accountId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getTokenSigningKey())
                .compact();
    }

    private String prepareRefreshToken(UUID sessionId, UUID accountId, LocalDateTime time) {
        log.info("Preparing refresh token for session {} and account {}.", sessionId, accountId);
        Date issuedAt = Date.from(Timestamp.valueOf(time).toInstant());
        Date expiration = Date.from(Timestamp.valueOf(time.plusMinutes(refreshTokenExpirationTime)).toInstant());

        return Jwts.builder()
                .setId(sessionId.toString())
                .setSubject("refresh_token")
                .claim("account_id", accountId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
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
        UUID sessionId = UUID.fromString(claims.getBody().getId());

        log.info("Validating session {}...", sessionId);
        SessionEntity session = sessionsRepository.findByIdAndActive(sessionId, true).orElseThrow(SessionNotFoundException::new);

        if(!claims.getBody().getSubject().equals("refresh_token") || session.getRefreshTokenExpiration().isBefore(LocalDateTime.now()) || !session.getRefreshToken().equals(refreshToken)) {
            session.setActive(false);
            sessionsRepository.save(session);
            log.info("Session {} expired. Logging out.", sessionId);
            throw new SessionExpiredException();
        }

        log.info("Session {} validated.", sessionId);

        return claims;
    }

    private SecretKeySpec getTokenSigningKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(tokenSigningSecret), SignatureAlgorithm.HS256.getJcaName());
    }
}
