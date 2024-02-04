package com.merchant.transactions.model;

import com.merchant.transactions.model.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;
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
@Entity(name = "merchants")
@DynamicUpdate
public class MerchantEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Email
    @NotEmpty
    private String  email;
    @Enumerated(EnumType.STRING)
    private MerchantStatus status;
    private BigDecimal totalTransactionSum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToMany(mappedBy = "merchant")
    private Set<AuthorizeTransactionEntity> transactions;
}
