package com.tickshop.payment.model.entities;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Table("customers")
public class Customer implements Persistable<UUID>, Serializable {

    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @Column("customer_id")
    private UUID customerId;
    @Column("name")
    private String name;
    @Column("balance")
    private BigDecimal balance;

    public Customer() {
    }

    public Customer(UUID customerId, String name, BigDecimal balance) {
        this.customerId = customerId;
        this.name = name;
        this.balance = balance;
    }

    @Override
    public @Nullable UUID getId() {
        return customerId;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
