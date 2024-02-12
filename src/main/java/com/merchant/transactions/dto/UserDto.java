package com.merchant.transactions.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String password;
}
