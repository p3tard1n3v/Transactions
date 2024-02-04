package com.merchant.transactions.mapper;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.MerchantEntity;

import java.util.stream.Collectors;

public class MerchantMapper {
    public static MerchantEntity mapToEntity(MerchantDto merchantDto) {
        return MerchantEntity.builder()
                .id(merchantDto.getId())
                .name(merchantDto.getName())
                .description(merchantDto.getDescription())
                .email(merchantDto.getEmail())
                .status(merchantDto.getStatus())
                .totalTransactionSum(merchantDto.getTotalTransactionSum())
                .user(merchantDto.getUser())
                .build();
    }

    public static MerchantDto mapToDto(MerchantEntity merchantEntity) {
        return MerchantDto.builder()
                .id(merchantEntity.getId())
                .name(merchantEntity.getName())
                .description(merchantEntity.getDescription())
                .email(merchantEntity.getEmail())
                .status(merchantEntity.getStatus())
                .totalTransactionSum(merchantEntity.getTotalTransactionSum())
                .user(merchantEntity.getUser())
                .transactions(merchantEntity.getTransactions().stream().map(trans -> AuthorizeTransactionMapper.mapToDto(trans)).collect(Collectors.toSet()))
                .build();
    }

}
