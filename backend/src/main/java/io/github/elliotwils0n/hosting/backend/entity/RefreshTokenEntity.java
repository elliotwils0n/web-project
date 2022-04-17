package io.github.elliotwils0n.hosting.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@Data
public class RefreshTokenEntity {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private AccountEntity account;

    @Column(name = "refresh_token", length = 400, nullable = false)
    private String refreshToken;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "used", nullable = false)
    private boolean used;

}
