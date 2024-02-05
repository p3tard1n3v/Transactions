package com.merchant.transactions.mapper;


import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.AuthorizeTransactionEntity;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorizeTransactionMapper {

    public static AuthorizeTransactionEntity mapToEntity(AuthorizeTransactionDto authorizeTransactionDto) {
        return AuthorizeTransactionEntity.builder()
                .id(authorizeTransactionDto.getId())
                .amount(authorizeTransactionDto.getAmount())
                .status(authorizeTransactionDto.getStatus())
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
                .status(transactionEntity.getStatus())
                .customerEmail(transactionEntity.getCustomerEmail())
                .customerPhone(transactionEntity.getCustomerPhone())
                .reference(transactionEntity.getReference())
                .merchant(transactionEntity.getMerchant())
                .created(transactionEntity.getCreated())
                .build().populateCreatedDate();
    }

    public static List<AuthorizeTransactionDto> mapToDto(List<AuthorizeTransactionEntity> transactionEntities) {
        return transactionEntities.stream().map(AuthorizeTransactionMapper::mapToDto).collect(Collectors.toList());
    }
}
