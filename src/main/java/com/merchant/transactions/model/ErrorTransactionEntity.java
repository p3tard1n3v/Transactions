package com.merchant.transactions.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("ERROR")
public class ErrorTransactionEntity extends AuthorizeTransactionEntity {
}
