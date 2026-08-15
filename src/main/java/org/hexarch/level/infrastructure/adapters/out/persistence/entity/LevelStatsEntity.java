package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Solo lectura desde JPA: los incrementos van por SQL atomico para no perder escrituras concurrentes.
@Entity
@Table(name = "level_stats")
public class LevelStatsEntity extends PanacheEntityBase {

    @Id
    @Column(name = "level_id", nullable = false, updatable = false)
    public UUID levelId;

    @Column(nullable = false)
    public long downloads = 0;

    @Column(nullable = false)
    public long likes = 0;

    @Column(nullable = false)
    public long plays = 0;
}
