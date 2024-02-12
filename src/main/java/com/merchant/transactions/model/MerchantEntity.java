package com.merchant.transactions.model;

import com.merchant.transactions.model.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("MERCHANT")
@DynamicUpdate
@Entity
public class MerchantEntity extends UserEntity {
    private String description;
    @Email
    @NotEmpty
    private String  email;
    @Enumerated(EnumType.STRING)
    private MerchantStatus status;
    private BigDecimal totalTransactionSum;

    @OneToMany(mappedBy = "merchant", fetch = FetchType.EAGER)
    private Set<AuthorizeTransactionEntity> transactions;
}
