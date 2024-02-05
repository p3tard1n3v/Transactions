package com.merchant.transactions.mapper;

import com.merchant.transactions.dto.ApprovedTransactionDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ApprovedTransactionMapper {
    public static ApprovedTransactionDto mapToDto(ApprovedTransactionEntity transactionEntity) {
        return ApprovedTransactionDto.builder()
                .id(transactionEntity.getId())
                .amount(transactionEntity.getAmount())
                .status(transactionEntity.getStatus())
                .reference(transactionEntity.getReference())
                .created(transactionEntity.getCreated())
                .build().populateCreatedDate();
    }

    public static List<ApprovedTransactionDto> mapToDto(List<ApprovedTransactionEntity> transactionEntities) {
        return transactionEntities.stream().map(ApprovedTransactionMapper::mapToDto).collect(Collectors.toList());
    }
}
