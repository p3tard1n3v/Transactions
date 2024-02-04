package com.merchant.transactions.service;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.AuthorizeTransactionEntity;

public interface AuthorizeTransactionService {
    AuthorizeTransactionEntity save(AuthorizeTransactionDto authorizeTransactionDto);
}
