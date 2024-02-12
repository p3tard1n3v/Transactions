package com.merchant.transactions.service.impl;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.RefundTransactionEntity;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.RefundTransactionRepository;
import com.merchant.transactions.service.ChargeAndRefundService;
import com.merchant.transactions.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeAndRefundServiceImpl implements ChargeAndRefundService {

    private final ApprovedTransactionRepository approvedTransactionRepository;
    private final RefundTransactionRepository refundTransactionRepository;
    private final MerchantService merchantService;

    @Autowired
    public ChargeAndRefundServiceImpl(final ApprovedTransactionRepository approvedTransactionRepository,
                                      final RefundTransactionRepository refundTransactionRepository,
                                                    final MerchantService merchantService) {
        this.approvedTransactionRepository = approvedTransactionRepository;
        this.refundTransactionRepository = refundTransactionRepository;
        this.merchantService = merchantService;
    }

    @Override
    public ApprovedTransactionEntity approve(AuthorizeTransactionEntity authorizeTransaction) {
        ApprovedTransactionEntity approvedTransaction = ApprovedTransactionEntity.builder()
                .amount(authorizeTransaction.getAmount())
                .reference(authorizeTransaction.getId())
                .merchant(authorizeTransaction.getMerchant())
                .build();

        ApprovedTransactionEntity transaction = approvedTransactionRepository.saveAndFlush(approvedTransaction);
        merchantService.updateTotalSum(authorizeTransaction.getMerchant().getId());

        return transaction;
    }

    @Override
    public RefundTransactionEntity refund(ApprovedTransactionEntity approvedTransaction) {
        RefundTransactionEntity refundTransaction = RefundTransactionEntity.builder()
                .amount(approvedTransaction.getAmount())
                .reference(approvedTransaction.getReference())
                .merchant(approvedTransaction.getMerchant())
                .build();

        RefundTransactionEntity refundedTransaction = refundTransactionRepository.saveAndFlush(refundTransaction);
        merchantService.updateTotalSum(approvedTransaction.getMerchant().getId());

        return refundedTransaction;
    }

}
