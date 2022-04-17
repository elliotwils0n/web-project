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
@Table(name = "access_tokens")
@NoArgsConstructor
@Data
public class AccessTokenEntity {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private AccountEntity account;

    @Column(name = "access_token", length = 400, nullable = false)
    private String accessToken;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

}
