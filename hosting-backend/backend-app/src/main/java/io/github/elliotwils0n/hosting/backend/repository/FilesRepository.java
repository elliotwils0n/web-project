package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByIdAndAccountId(Long id, UUID accountId);

    List<FileEntity> findAllByAccountId(UUID accountId);
}
