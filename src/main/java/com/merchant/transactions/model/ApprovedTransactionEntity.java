package com.merchant.transactions.model;

import com.merchant.transactions.model.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "transactions")
@DiscriminatorValue("ApprovedTransactionEntity")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ApprovedTransactionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharJdbcType.class)
    private UUID id;

    private BigDecimal amount;

    private TransactionStatus status;

    @Column(name = "reference_id")
    @JdbcType(VarcharJdbcType.class)
    private UUID reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private MerchantEntity merchant;
}
