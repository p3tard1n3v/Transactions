package com.merchant.transactions.service;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.service.impl.NotAllowedOperationRefundException;


public interface ChargeAndRefundService {
    ApprovedTransactionEntity approve(AuthorizeTransactionEntity authorizeTransaction);

    ApprovedTransactionEntity refund(ApprovedTransactionEntity approvedTransaction) throws NotAllowedOperationRefundException;
}
