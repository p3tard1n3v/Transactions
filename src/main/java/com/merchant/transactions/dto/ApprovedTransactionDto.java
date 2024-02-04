package com.merchant.transactions.dto;

import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@SuperBuilder
public class ApprovedTransactionDto {
    private UUID id;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private UUID reference;
}
