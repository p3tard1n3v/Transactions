package com.merchant.transactions.security;

import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    @Autowired
    public CustomUserDetailsService(final UserRepository userRepository, final MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByName(username);
        if(user != null) {
            return new User(
                    user.getName(),
                    user.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority(user.getClass().getSimpleName()))
            );
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
