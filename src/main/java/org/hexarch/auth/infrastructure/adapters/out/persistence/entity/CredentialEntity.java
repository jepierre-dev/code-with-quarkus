package org.hexarch.auth.infrastructure.adapters.out.persistence.entity;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// El id es el del usuario: la FK contra users la mantiene Liquibase, no una relacion JPA entre contextos.
@Entity
@Table(name = "user_credential")
public class CredentialEntity extends PanacheEntityBase {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    public UUID userId;

    @Column(name = "pass_hash", nullable = false, columnDefinition = "text")
    public String passHash;
}
