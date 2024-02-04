package com.merchant.transactions.repository;

import com.merchant.transactions.model.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
}
