package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.mapper.AuthorizeTransactionMapper;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.AuthorizeTransactionRepository;
import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final AuthorizeTransactionRepository authorizeTransactionRepository;
    private final ApprovedTransactionRepository approvedTransactionRepository;

    @Autowired
    public TransactionServiceImpl(final AuthorizeTransactionRepository authorizeTransactionRepository,
                                  final ApprovedTransactionRepository approvedTransactionRepository) {
        this.authorizeTransactionRepository = authorizeTransactionRepository;
        this.approvedTransactionRepository = approvedTransactionRepository;
    }
    @Override
    public AuthorizeTransactionEntity save(AuthorizeTransactionDto transactionDto) {
        AuthorizeTransactionEntity authorizeTransaction = AuthorizeTransactionMapper.mapToEntity(transactionDto);
        authorizeTransaction = save(authorizeTransaction);
        return authorizeTransaction;
    }

    @Override
    public void deleteOldThanOneHourTransactions() {
        LocalDateTime nowMinusHour = LocalDateTime.now().minusHours(1);
        approvedTransactionRepository.deleteAllByLastUpdatedLessThan(nowMinusHour);
        authorizeTransactionRepository.deleteAllByLastUpdatedLessThanWithoutReferences(nowMinusHour);
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
