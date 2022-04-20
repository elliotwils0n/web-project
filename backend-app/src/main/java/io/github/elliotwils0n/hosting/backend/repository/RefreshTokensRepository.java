package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByAccountId(UUID accountId);
}
