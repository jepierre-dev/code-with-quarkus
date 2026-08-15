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
@Table(name = "player_stats")
public class PlayerStatsEntity extends PanacheEntityBase {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    public UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_player_stats_user_id"))
    public UserEntity user;

    @Column(nullable = false)
    public long stars = 0;

    @Column(nullable = false)
    public long diamonds = 0;

    @Column(name = "secret_coins", nullable = false)
    public long secretCoins = 0;

    @Column(name = "demons_completed", nullable = false)
    public long demonsCompleted = 0;

    @Column(name = "levels_completed", nullable = false)
    public long levelsCompleted = 0;

    @Column(name = "creator_points", nullable = false)
    public long creatorPoints = 0;
}
