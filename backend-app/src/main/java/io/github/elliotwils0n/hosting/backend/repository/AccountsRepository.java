package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountsRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByUsername(String username);

}
