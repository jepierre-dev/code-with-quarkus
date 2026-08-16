package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "level_versions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_level_versions_level_id_version_number", columnNames = { "level_id",
                "version_number" }) })
public class LevelVersionEntity extends PanacheEntityBase {

    // Sin @GeneratedValue: el id lo genera el dominio.
    @Id
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(name = "level_id", nullable = false)
    public UUID levelId;

    @Column(name = "version_number", nullable = false)
    public int versionNumber;

    @Column(name = "created_by", nullable = false)
    public UUID createdBy;

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
