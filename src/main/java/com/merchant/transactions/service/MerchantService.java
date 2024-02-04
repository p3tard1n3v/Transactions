package com.merchant.transactions.service;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.MerchantEntity;

public interface MerchantService {
    MerchantEntity findById(Long merchantId);

    MerchantEntity save(MerchantDto merchantDto);
    void updateTotalSum(Long merchantId);
}
