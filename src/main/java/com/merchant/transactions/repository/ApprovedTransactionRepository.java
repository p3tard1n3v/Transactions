package com.merchant.transactions.repository;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApprovedTransactionRepository extends JpaRepository<ApprovedTransactionEntity, UUID> {
    @Query(value = "SELECT * FROM transactions WHERE merchant_id = ?1 and dtype='ApprovedTransactionEntity'"
            , nativeQuery = true)
    List<ApprovedTransactionEntity> findAllByMerchantId(Long merchantId);

    @Transactional
    void deleteAllByLastUpdatedLessThan(LocalDateTime time);
}
