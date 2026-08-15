package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.user.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "level_versions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_level_versions_level_id_version_number", columnNames = { "level_id",
                "version_number" }) })
public class LevelVersionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false, foreignKey = @ForeignKey(name = "fk_level_versions_level_id"))
    public LevelEntity level;

    @Column(name = "version_number", nullable = false)
    public int versionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_level_versions_created_by"))
    public UserEntity createdBy;

    // Sin @Lob: en PostgreSQL eso lo mapearia a OID en vez de bytea.
    @Column(name = "level_data", nullable = false, columnDefinition = "bytea")
    public byte[] levelData;

    @Column(nullable = false)
    public String checksum;

    @Column(columnDefinition = "text")
    public String changelog;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
}
