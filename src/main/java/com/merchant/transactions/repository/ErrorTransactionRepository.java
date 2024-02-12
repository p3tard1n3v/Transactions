package com.merchant.transactions.repository;

import com.merchant.transactions.model.ErrorTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ErrorTransactionRepository extends JpaRepository<ErrorTransactionEntity, UUID> { }
