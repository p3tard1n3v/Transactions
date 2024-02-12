package com.merchant.transactions.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("REVERSAL")
public class ReversalTransactionEntity extends AuthorizeTransactionEntity {
}
