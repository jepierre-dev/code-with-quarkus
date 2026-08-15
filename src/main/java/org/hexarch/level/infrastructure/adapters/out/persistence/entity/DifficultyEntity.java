package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "difficulties")
public class DifficultyEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(nullable = false, columnDefinition = "text")
    public String name;

    @Column(nullable = false)
    public long stars;

    @Column(nullable = false, columnDefinition = "text")
    public String icon;
}
