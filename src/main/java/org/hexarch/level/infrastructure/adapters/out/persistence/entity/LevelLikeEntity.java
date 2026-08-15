package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "level_likes")
public class LevelLikeEntity extends PanacheEntityBase {

    @EmbeddedId
    public LevelLikeId id = new LevelLikeId();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Embeddable
    public static class LevelLikeId implements Serializable {

        @Column(name = "level_id", nullable = false)
        public UUID levelId;

        @Column(name = "user_id", nullable = false)
        public UUID userId;

        public LevelLikeId() {
        }

        public LevelLikeId(UUID levelId, UUID userId) {
            this.levelId = levelId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LevelLikeId that)) {
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
