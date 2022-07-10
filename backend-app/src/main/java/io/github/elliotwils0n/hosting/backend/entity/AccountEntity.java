package io.github.elliotwils0n.hosting.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="accounts")
@NoArgsConstructor
@Data
public class AccountEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "username", length = 30, nullable = false)
    private String username;

    @Column(name = "password", length = 60, nullable = false)
    private String passwordHash;

//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<SessionEntity> sessionEntity = new ArrayList<>();


    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FileEntity> files;

    public AccountEntity(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

}
