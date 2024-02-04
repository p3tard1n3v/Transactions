package com.merchant.transactions.repository;

import com.merchant.transactions.model.AuthorizeTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorizeTransactionRepository extends JpaRepository<AuthorizeTransactionEntity, UUID> {
}
