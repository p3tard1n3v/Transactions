package com.merchant.transactions.dto;

import com.merchant.transactions.model.enums.MerchantStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MerchantDto extends UserDto{
    private String description;
    @Email
    @NotEmpty
    private String  email;
    private MerchantStatus status;
    private BigDecimal totalTransactionSum;
    private Set<AuthorizeTransactionDto> transactions;
    private LocalDateTime created;
}
