package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.model.AccountDeletedEvent;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.repository.AccountsRepository;
import io.github.elliotwils0n.hosting.backend.service.AccountsServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountsService implements AccountsServiceInterface {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AccountDto createAccount(Credentials credentials) {
        if(accountsRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException();
        }
        AccountEntity account = accountsRepository.save(new AccountEntity(credentials.getUsername(), passwordEncoder.encode(credentials.getPassword())));
        log.info("Account created for {}", credentials.getUsername());
        return new AccountDto(account.getId(), account.getUsername(), account.getPasswordHash());
    }

    @Override
    @Transactional
    public void deleteAccount(UUID accountId) {
        AccountEntity account = accountsRepository.findById(accountId).orElseThrow(AccountDoesNotExist::new);

        List<FileDto> accountFiles = account.getFiles().stream()
                .map(file -> new FileDto(file.getId(), file.getUploadedAt(), file.getOriginalFilename()))
                .collect(Collectors.toList());
        eventPublisher.publishEvent(new AccountDeletedEvent(this, accountId, accountFiles));

        accountsRepository.delete(account);
        log.info("Account {} has been deleted.", accountId);
    }

    @Override
    public boolean isPasswordValid(Credentials credentials) {
        AccountEntity account = accountsRepository.findByUsername(credentials.getUsername()).orElseThrow(AccountDoesNotExist::new);

        return passwordEncoder.matches(credentials.getPassword(), account.getPasswordHash());
    }

    public AccountDto findByUsername(String username) {
        AccountEntity account = accountsRepository.findByUsername(username).orElseThrow(AccountDoesNotExist::new);

        return new AccountDto(account.getId(), account.getUsername(), account.getPasswordHash());
    }

}
