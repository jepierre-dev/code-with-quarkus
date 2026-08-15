package org.hexarch.user.infrastructure.adapters.out.persistence.entity;

import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformRole;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "uk_users_email", columnNames = { "email" }) })
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(nullable = false, columnDefinition = "text")
    public String name;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    public String email;

    @Column(name = "is_banned", nullable = false)
    public boolean banned = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "user_role")
    public PlatformRole role = PlatformRole.PLAYER;
}
