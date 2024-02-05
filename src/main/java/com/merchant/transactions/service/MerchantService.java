package com.merchant.transactions.service;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.MerchantEntity;

import java.util.List;

public interface MerchantService {
    MerchantEntity findById(Long merchantId);

    MerchantDto findDtoById(Long merchantId);

    MerchantEntity save(MerchantDto merchantDto);
    void updateTotalSum(Long merchantId);
    List<MerchantDto> findByUserName(String username);
    List<MerchantDto> findAll();

    int transactionsCountById(Long merchantId);

    void updateMerchant(MerchantDto merchantDto);

    void delete(Long merchantId);
}
