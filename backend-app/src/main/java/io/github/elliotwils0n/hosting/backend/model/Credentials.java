package io.github.elliotwils0n.hosting.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Credentials {

    private String username;
    private String password;

}
