package com.merchant.transactions.service;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.RefundTransactionEntity;


public interface ChargeAndRefundService {
    ApprovedTransactionEntity approve(AuthorizeTransactionEntity authorizeTransaction);
    RefundTransactionEntity refund(ApprovedTransactionEntity approvedTransaction);
}
