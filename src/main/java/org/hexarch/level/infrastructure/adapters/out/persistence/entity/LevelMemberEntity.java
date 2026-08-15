package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "level_members")
public class LevelMemberEntity extends PanacheEntityBase {

    @EmbeddedId
    public LevelMemberId id = new LevelMemberId();

    @MapsId("levelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", foreignKey = @ForeignKey(name = "fk_level_members_level_id"))
    public LevelEntity level;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "level_role")
    public LevelRole role;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    public LocalDateTime joinedAt;

    @Column(name = "invited_by")
    public UUID invitedBy;

    @Embeddable
    public static class LevelMemberId implements Serializable {

        @Column(name = "level_id", nullable = false)
        public UUID levelId;

        @Column(name = "user_id", nullable = false)
        public UUID userId;

        public LevelMemberId() {
        }

        public LevelMemberId(UUID levelId, UUID userId) {
            this.levelId = levelId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LevelMemberId that)) {
                return false;
            }
            return Objects.equals(levelId, that.levelId) && Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(levelId, userId);
        }
    }
}
