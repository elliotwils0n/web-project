package io.github.elliotwils0n.hosting.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto {

    private UUID id;
    private String username;
    private String passwordHash;

}
