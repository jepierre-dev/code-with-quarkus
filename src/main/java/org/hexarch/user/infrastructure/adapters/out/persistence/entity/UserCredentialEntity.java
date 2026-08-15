package org.hexarch.user.infrastructure.adapters.out.persistence.entity;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_credential")
public class UserCredentialEntity extends PanacheEntityBase {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    public UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_credential_user_id"))
    public UserEntity user;

    @Column(name = "pass_hash", nullable = false, columnDefinition = "text")
    public String passHash;
}
