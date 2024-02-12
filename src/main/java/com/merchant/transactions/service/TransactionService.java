package com.merchant.transactions.service;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;

import java.util.List;

public interface TransactionService {
    AuthorizeTransactionEntity save(AuthorizeTransactionDto authorizeTransactionDto);
    void save(AuthorizeTransactionDto authorizeTransactionDto, MerchantEntity merchantEntity);
    void deleteOldThanOneHourTransactions();
    List<AuthorizeTransactionDto> findByMerchant(Long merchant);
}
