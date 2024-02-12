package com.merchant.transactions.repository;

import com.merchant.transactions.model.ReversalTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReversalTransactionRepository extends JpaRepository<ReversalTransactionEntity, UUID> {
}
