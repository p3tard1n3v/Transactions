package com.merchant.transactions.repository;

import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuthorizeTransactionRepository extends JpaRepository<AuthorizeTransactionEntity, UUID> {
    @Modifying
    @Transactional
    @Query(value = "DELETE t1 FROM transactions t1 left join transactions t2 on t1.id=t2.reference_id " +
            "WHERE t2.reference_id is null and t1.dtype='AuthorizeTransactionEntity' and t1.last_updated<?1", nativeQuery = true)
    void deleteAllByLastUpdatedLessThanWithoutReferences(LocalDateTime time);

    List<AuthorizeTransactionEntity> findAllByMerchant(MerchantEntity merchant);
}
