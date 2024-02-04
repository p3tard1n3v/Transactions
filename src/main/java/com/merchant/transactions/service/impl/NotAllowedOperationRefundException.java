package com.merchant.transactions.service.impl;

import com.merchant.transactions.model.enums.TransactionStatus;

public class NotAllowedOperationRefundException extends Exception {
    @Override
    public String getMessage() {
        return "refund operation cannot be execute on transaction that is not in status=" + TransactionStatus.APPROVED;
    }
}
