package com.merchant.transactions.mapper;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.MerchantEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MerchantMapper {
    public static MerchantEntity mapToEntity(MerchantDto merchantDto) {
        return MerchantEntity.builder()
                .id(merchantDto.getId())
                .name(merchantDto.getName())
                .password(merchantDto.getPassword())
                .description(merchantDto.getDescription())
                .email(merchantDto.getEmail())
                .status(merchantDto.getStatus())
                .totalTransactionSum(merchantDto.getTotalTransactionSum())
                .build();
    }

    public static MerchantDto mapToDto(MerchantEntity merchantEntity) {
        return MerchantDto.builder()
                .id(merchantEntity.getId() != null ? merchantEntity.getId() : null)
                .name(merchantEntity.getName())
                .description(merchantEntity.getDescription())
                .email(merchantEntity.getEmail())
                .status(merchantEntity.getStatus())
                .totalTransactionSum(merchantEntity.getTotalTransactionSum())
                .created(merchantEntity.getCreated())
                .transactions(merchantEntity.getTransactions().stream().map(trans -> AuthorizeTransactionMapper.mapToDto(trans)).collect(Collectors.toSet()))
                .build();
    }

    public static List<MerchantDto> mapToDto(List<MerchantEntity> merchantEntities) {
        return merchantEntities.stream().map(MerchantMapper::mapToDto).collect(Collectors.toList());
    }

}
