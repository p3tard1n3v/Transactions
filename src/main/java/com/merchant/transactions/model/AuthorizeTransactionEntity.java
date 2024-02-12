package com.merchant.transactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("AUTHORIZED")
public class AuthorizeTransactionEntity extends ApprovedTransactionEntity {
    @Email
    @NotEmpty
    @Column(name="customer_email")
    private String customerEmail;

    @Size(min = 10, max = 17, message = "Number should have at least 10 or less than 17 digits")
    @Column(name="customer_phone")
    private String customerPhone;
}
