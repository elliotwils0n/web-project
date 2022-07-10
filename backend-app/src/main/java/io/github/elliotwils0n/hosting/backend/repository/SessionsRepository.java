package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.SessionEntity;
import io.github.elliotwils0n.hosting.backend.model.TokenPairProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionsRepository extends JpaRepository<SessionEntity, UUID> {

    Optional<SessionEntity> findFirstByAccountIdAndActiveOrderByModificationTimeDesc(UUID accountId, boolean active);

    List<SessionEntity> findAllByAccountIdAndActive(UUID accountId, boolean active);

    Optional<SessionEntity> findByIdAndActive(UUID sessionId, boolean active);

    Optional<TokenPairProjection> findAccessTokenAndRefreshTokenByAccountIdAndActive(UUID accountId, boolean active);

}
