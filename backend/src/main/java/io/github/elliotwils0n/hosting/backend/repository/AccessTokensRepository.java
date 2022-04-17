package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.AccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessTokensRepository extends JpaRepository<AccessTokenEntity, UUID> {

    Optional<AccessTokenEntity> findByAccountId(UUID accountId);
}
