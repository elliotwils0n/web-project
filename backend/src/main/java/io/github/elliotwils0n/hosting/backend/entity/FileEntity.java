package io.github.elliotwils0n.hosting.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@NoArgsConstructor
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "account_id", nullable = false)
    public UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "account_id", nullable = false, insertable = false, updatable = false)
    private AccountEntity account;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "original_filename", length = 400, nullable = false)
    private String originalFilename;

    @Column(name = "encryption_key", nullable = false)
    private byte[] encryptionKey;


    public FileEntity(UUID accountId, String originalFilename, byte[] encryptionKey) {
        this.accountId = accountId;
        this.originalFilename = originalFilename;
        this.encryptionKey = encryptionKey;
    }

}
