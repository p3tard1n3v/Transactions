package com.merchant.transactions.dto;

import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
public class AuthorizeTransactionDto extends ApprovedTransactionDto {
    @Email
    @NotEmpty
    private String customerEmail;
    private String customerPhone;
    private MerchantEntity merchant;
}
