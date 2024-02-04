package com.merchant.transactions.dto;

import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class MerchantDto {
    private Long id;
    private String name;
    private String description;
    @Email
    @NotEmpty
    private String  email;
    MerchantStatus status;
    private BigDecimal totalTransactionSum;
    private UserEntity user;
    private Set<AuthorizeTransactionDto> transactions;
}
