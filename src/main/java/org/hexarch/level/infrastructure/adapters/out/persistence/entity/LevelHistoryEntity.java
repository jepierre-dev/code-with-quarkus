package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "level_history")
public class LevelHistoryEntity extends PanacheEntityBase {

    // Sin @GeneratedValue: el id lo genera el dominio.
    @Id
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(name = "level_id", nullable = false)
    public UUID levelId;

    // Nulo cuando la accion la ejecuta el sistema y no un usuario.
    @Column(name = "actor_id")
    public UUID actorId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "level_action")
    public LevelAction action;

    @Column(name = "target_user_id")
    public UUID targetUserId;

    @Column(name = "version_id")
    public UUID versionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
}
