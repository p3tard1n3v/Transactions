package com.merchant.transactions.service.impl;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.service.ChargeAndRefundService;
import com.merchant.transactions.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeAndRefundServiceImpl implements ChargeAndRefundService {

    private ApprovedTransactionRepository approvedTransactionRepository;
    private MerchantService merchantService;

    @Autowired
    public ChargeAndRefundServiceImpl(final ApprovedTransactionRepository approvedTransactionRepository,
                                                    final MerchantService merchantService) {
        this.approvedTransactionRepository = approvedTransactionRepository;
        this.merchantService = merchantService;
    }

    @Override
    public ApprovedTransactionEntity approve(AuthorizeTransactionEntity authorizeTransaction) {
        ApprovedTransactionEntity approvedTransaction = ApprovedTransactionEntity.builder()
                .amount(authorizeTransaction.getAmount())
                .reference(authorizeTransaction.getId())
                .merchant(authorizeTransaction.getMerchant())
                .status(TransactionStatus.APPROVED)
                .build();

        ApprovedTransactionEntity transaction = approvedTransactionRepository.saveAndFlush(approvedTransaction);
        merchantService.updateTotalSum(authorizeTransaction.getMerchant().getId());

        return transaction;
    }

    @Override
    public ApprovedTransactionEntity refund(ApprovedTransactionEntity approvedTransaction)
            throws NotAllowedOperationRefundException {
        if (approvedTransaction.getStatus() == null || !approvedTransaction.getStatus().equals(TransactionStatus.APPROVED)) {
            throw new NotAllowedOperationRefundException();
        }
        ApprovedTransactionEntity refundTransaction = ApprovedTransactionEntity.builder()
                .amount(approvedTransaction.getAmount())
                .reference(approvedTransaction.getReference())
                .merchant(approvedTransaction.getMerchant())
                .status(TransactionStatus.REFUNDED)
                .build();

        ApprovedTransactionEntity refundedTransaction = approvedTransactionRepository.saveAndFlush(refundTransaction);
        merchantService.updateTotalSum(approvedTransaction.getMerchant().getId());

        return refundedTransaction;
    }

}
