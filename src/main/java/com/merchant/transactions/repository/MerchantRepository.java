package com.merchant.transactions.repository;

import com.merchant.transactions.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    MerchantEntity findByName(String name);
}
