package com.merchant.transactions.mapper;


import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.ErrorTransactionEntity;
import com.merchant.transactions.model.ReversalTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizeTransactionMapper {

    public static AuthorizeTransactionEntity mapToEntity(AuthorizeTransactionDto authorizeTransactionDto) {
        return AuthorizeTransactionEntity.builder()
                .id(authorizeTransactionDto.getId())
                .amount(authorizeTransactionDto.getAmount())
                .customerEmail(authorizeTransactionDto.getCustomerEmail())
                .customerPhone(authorizeTransactionDto.getCustomerPhone())
                .reference(authorizeTransactionDto.getReference())
                .merchant(authorizeTransactionDto.getMerchant())
                .build();
    }

    public static AuthorizeTransactionDto mapToDto(AuthorizeTransactionEntity transactionEntity) {
        return AuthorizeTransactionDto.builder()
                .id(transactionEntity.getId())
                .amount(transactionEntity.getAmount())
                .customerEmail(transactionEntity.getCustomerEmail())
                .customerPhone(transactionEntity.getCustomerPhone())
                .reference(transactionEntity.getReference())
                .status(TransactionStatus.getStatus(transactionEntity.getClass()))
                .merchant(transactionEntity.getMerchant())
                .created(transactionEntity.getCreated())
                .build().populateCreatedDate();
    }

    public static ErrorTransactionEntity mapToErrorTransaction(AuthorizeTransactionEntity transactionEntity) {
        return ErrorTransactionEntity.builder()
                .id(transactionEntity.getId())
                .amount(transactionEntity.getAmount())
                .customerEmail(transactionEntity.getCustomerEmail())
                .customerPhone(transactionEntity.getCustomerPhone())
                .reference(transactionEntity.getReference())
                .merchant(transactionEntity.getMerchant())
                .created(transactionEntity.getCreated())
                .build();
    }

    public static ReversalTransactionEntity mapToReversalTransaction(AuthorizeTransactionEntity transactionEntity) {
        return ReversalTransactionEntity.builder()
                .id(transactionEntity.getId())
                .amount(transactionEntity.getAmount())
                .customerEmail(transactionEntity.getCustomerEmail())
                .customerPhone(transactionEntity.getCustomerPhone())
                .reference(transactionEntity.getReference())
                .merchant(transactionEntity.getMerchant())
                .created(transactionEntity.getCreated())
                .build();
    }

    public static List<AuthorizeTransactionDto> mapToDto(List<AuthorizeTransactionEntity> transactionEntities) {
        return transactionEntities.stream().map(AuthorizeTransactionMapper::mapToDto).collect(Collectors.toList());
    }
}
