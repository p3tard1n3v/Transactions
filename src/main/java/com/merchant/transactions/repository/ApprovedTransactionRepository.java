package com.merchant.transactions.repository;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ApprovedTransactionRepository extends JpaRepository<ApprovedTransactionEntity, UUID> {
    @Query(value = "SELECT * FROM transactions WHERE merchant_id = ?1", nativeQuery = true)
    List<ApprovedTransactionEntity> findByMerchantId(Long merchantId);
}
