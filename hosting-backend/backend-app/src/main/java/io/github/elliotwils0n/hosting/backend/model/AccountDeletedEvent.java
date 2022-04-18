package io.github.elliotwils0n.hosting.backend.model;

import io.github.elliotwils0n.hosting.backend.dto.FileDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class AccountDeletedEvent extends ApplicationEvent {

    private final UUID accountId;
    private final List<FileDto> accountFiles;

    public AccountDeletedEvent(Object source, UUID accountId, List<FileDto> accountFiles) {
        super(source);
        this.accountId = accountId;
        this.accountFiles = accountFiles;
    }
}
