package com.merchant.transactions.repository;

import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthorizeTransactionRepository extends JpaRepository<AuthorizeTransactionEntity, UUID> {
    List<AuthorizeTransactionEntity> findAllByMerchant(MerchantEntity merchant);
}
