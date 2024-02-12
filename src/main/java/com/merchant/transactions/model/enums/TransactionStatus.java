package com.merchant.transactions.model.enums;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.ErrorTransactionEntity;
import com.merchant.transactions.model.RefundTransactionEntity;
import com.merchant.transactions.model.ReversalTransactionEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TransactionStatus {
    APPROVED(ApprovedTransactionEntity.class),
    REVERSED(ReversalTransactionEntity.class),
    REFUNDED(RefundTransactionEntity.class),
    ERROR(ErrorTransactionEntity.class);

    private static final Map<Class<? extends ApprovedTransactionEntity>, TransactionStatus> lookup = new HashMap<>();

    static {
        Arrays.stream(TransactionStatus.values())
                .forEach(status -> lookup.put(status.classTrans, status));
    }

    public static TransactionStatus getStatus(Class<? extends ApprovedTransactionEntity> classTrans) {
        return lookup.get(classTrans);
    }

    private final Class<? extends ApprovedTransactionEntity> classTrans;
    TransactionStatus(Class<? extends ApprovedTransactionEntity> classTrans){
        this.classTrans = classTrans;
    }



}
