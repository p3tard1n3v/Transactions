package com.merchant.transactions.repository;

import com.merchant.transactions.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    @Query(value = "SELECT m.* FROM merchants m inner join users s on s.id=m.user_id WHERE s.username = ?1", nativeQuery = true)
    List<MerchantEntity> findByUserName(String username);

    @Query(value = "SELECT count(*) FROM merchants m inner join transactions t on t.merchant_id=m.id WHERE m.id = ?1", nativeQuery = true)
    int transactionsCountById(Long id);
}
