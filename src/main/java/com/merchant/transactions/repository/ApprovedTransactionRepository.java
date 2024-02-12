package com.merchant.transactions.repository;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApprovedTransactionRepository extends JpaRepository<ApprovedTransactionEntity, UUID> {
    List<ApprovedTransactionEntity> findAllByMerchantId(Long merchantId);

    int countAllByMerchantId(Long merchantId);

    @Transactional
    void deleteAllByLastUpdatedLessThan(LocalDateTime time);
}
