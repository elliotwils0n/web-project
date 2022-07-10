package io.github.elliotwils0n.hosting.backend.model;

import java.time.LocalDateTime;

public interface TokenPairProjection {

    String getAccessToken();
    String getRefreshToken();

}
