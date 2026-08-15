package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelAction;
import org.hexarch.user.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "level_history")
public class LevelHistoryEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false, foreignKey = @ForeignKey(name = "fk_level_history_level_id"))
    public LevelEntity level;

    // Nulo cuando la accion la ejecuta el sistema y no un usuario.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", foreignKey = @ForeignKey(name = "fk_level_history_actor_id"))
    public UserEntity actor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "level_action")
    public LevelAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", foreignKey = @ForeignKey(name = "fk_level_history_target_user_id"))
    public UserEntity targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", foreignKey = @ForeignKey(name = "fk_level_history_version_id"))
    public LevelVersionEntity version;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    public String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
}
