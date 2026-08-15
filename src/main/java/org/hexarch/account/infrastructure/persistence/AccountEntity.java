package org.hexarch.account.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "holder_name" }),
        @UniqueConstraint(columnNames = { "email" }) })
public class AccountEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(name = "holder_name", nullable = false, unique = true)
    public String holderName;

    @Column(nullable = false, precision = 19, scale = 2)
    public BigDecimal balance;

    @Column(nullable = true, unique = true)
    public String email;
}
