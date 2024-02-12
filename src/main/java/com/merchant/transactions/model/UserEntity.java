package com.merchant.transactions.model;

import com.merchant.transactions.security.SecurityConfig;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "merchants")
@DiscriminatorValue("ADMIN_USER")
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String name;
    private String password;

    @PrePersist
    public void prePersist(){
        password = SecurityConfig.passwordEncoder().encode(password);
    }
}
