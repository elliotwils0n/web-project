package io.github.elliotwils0n.hosting.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="sessions")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionEntity {

    @Id
    private UUID id;

    @Column(name = "account_id")
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.DETACH)
    @JoinColumn(name = "account_id", nullable = false, insertable = false, updatable = false)
    private AccountEntity account;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "access_token", length = 400, nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", length = 400, nullable = false)
    private String refreshToken;

    @Column(name = "access_token_expiration", nullable = false)
    private LocalDateTime accessTokenExpiration;

    @Column(name = "refresh_token_expiration", nullable = false)
    private LocalDateTime refreshTokenExpiration;

    @Column(name = "modification_time", nullable = false)
    private LocalDateTime modificationTime;

}
