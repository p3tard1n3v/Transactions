package com.merchant.transactions.dto;

import com.merchant.transactions.model.enums.UserRole;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private UserRole role;
}
