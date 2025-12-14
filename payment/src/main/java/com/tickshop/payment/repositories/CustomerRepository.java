package com.tickshop.payment.repositories;

import com.tickshop.payment.model.entities.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {
}
