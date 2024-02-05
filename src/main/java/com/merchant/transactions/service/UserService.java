package com.merchant.transactions.service;

import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.UserEntity;

public interface  UserService {
    UserEntity save(UserDto user);
    UserEntity findByUsername(String username);
    long usersCount();
    boolean isAdmin(String username);
}
