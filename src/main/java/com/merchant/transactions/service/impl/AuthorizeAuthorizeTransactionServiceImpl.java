package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.mapper.AuthorizeTransactionMapper;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.AuthorizeTransactionRepository;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.AuthorizeTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthorizeAuthorizeTransactionServiceImpl implements AuthorizeTransactionService {
    private AuthorizeTransactionRepository authorizeTransactionRepository;

    @Autowired
    public AuthorizeAuthorizeTransactionServiceImpl(final AuthorizeTransactionRepository authorizeTransactionRepository) {
        this.authorizeTransactionRepository = authorizeTransactionRepository;
    }
    @Override
    public AuthorizeTransactionEntity save(AuthorizeTransactionDto transactionDto) {
        AuthorizeTransactionEntity authorizeTransaction = AuthorizeTransactionMapper.mapToEntity(transactionDto);
        authorizeTransaction = save(authorizeTransaction);
        return authorizeTransaction;
    }

    private AuthorizeTransactionEntity save(AuthorizeTransactionEntity transaction) {
        if (transaction.getReference() != null) {
            transaction.setStatus(TransactionStatus.ERROR);
        } else if (transaction.getAmount().equals(BigDecimal.ZERO)) {
            transaction.setStatus(TransactionStatus.REVERSED);
        }

        return authorizeTransactionRepository.save(transaction);
    }
}
