package io.github.elliotwils0n.hosting.backend.repository;

import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByIdAndAccount(Long id, AccountEntity account);

    List<FileEntity> findAllByAccount(AccountEntity account);
}
