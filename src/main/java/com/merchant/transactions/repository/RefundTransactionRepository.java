package com.merchant.transactions.repository;

import com.merchant.transactions.model.RefundTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundTransactionRepository extends JpaRepository<RefundTransactionEntity, UUID> {
}
