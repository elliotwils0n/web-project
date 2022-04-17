package io.github.elliotwils0n.hosting.backend.service.implementation;

import io.github.elliotwils0n.hosting.backend.dto.AccountDto;
import io.github.elliotwils0n.hosting.backend.entity.AccountEntity;
import io.github.elliotwils0n.hosting.backend.model.Credentials;
import io.github.elliotwils0n.hosting.backend.repository.AccountsRepository;
import io.github.elliotwils0n.hosting.backend.service.AccountsServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsService implements AccountsServiceInterface {

    @Autowired
    private AccountsRepository accountsRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AccountDto createAccount(Credentials credentials) {
        if(accountsRepository.findByUsername(credentials.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException();
        }
        AccountEntity account = accountsRepository.save(new AccountEntity(credentials.getUsername(), passwordEncoder.encode(credentials.getPassword())));
        return new AccountDto(account.getId(), account.getUsername(), account.getPasswordHash());
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
