package com.merchant.transactions.security;

import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if(user != null) {
            return new User(
                    user.getUsername(),
                    user.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority(user.getRole().name()))
            );
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
