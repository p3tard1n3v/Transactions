package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.repository.UserRepository;
import com.merchant.transactions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity save(final UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());

        return userRepository.save(user);
    }

    @Override
    public UserEntity findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public long usersCount() {
        return userRepository.count();
    }
}
