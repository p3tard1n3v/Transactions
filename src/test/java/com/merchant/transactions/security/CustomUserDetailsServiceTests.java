package com.merchant.transactions.security;

import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testLoadUserByUsername() {
        String username = "test";
        UserEntity user = mock(UserEntity.class);
        when(user.getName()).thenReturn("test");
        when(user.getPassword()).thenReturn("edsgsdfgsdgsfdg3432412adsdas4r");
        when(userRepository.findByName(username)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertThat("test").isEqualTo(userDetails.getUsername());
        assertThat("edsgsdfgsdgsfdg3432412adsdas4r").isEqualTo(userDetails.getPassword());
        assertThat(1).isEqualTo(userDetails.getAuthorities().size());
        assertThat(true).isEqualTo(userDetails.getAuthorities()
                .stream()
                .anyMatch(e -> e.getAuthority()
                        .equals(UserEntity.class.getSimpleName())));
    }

    @Test
    public void testLoadUserByUsernameWhenMerchant() {
        String username = "test";
        MerchantEntity user = mock(MerchantEntity.class);
        when(user.getName()).thenReturn("test");
        when(user.getPassword()).thenReturn("edsgsdfgsdgsfdg3432412adsdas4r");
        when(userRepository.findByName(username)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertThat("test").isEqualTo(userDetails.getUsername());
        assertThat("edsgsdfgsdgsfdg3432412adsdas4r").isEqualTo(userDetails.getPassword());
        assertThat(1).isEqualTo(userDetails.getAuthorities().size());
        assertThat(true).isEqualTo(userDetails.getAuthorities()
                .stream()
                .anyMatch(e -> e.getAuthority()
                        .equals(MerchantEntity.class.getSimpleName())));
    }

    @Test
    public void testLoadUserByUsernameThrowsExceptionWhenNoUserByUsername() {
        String username = "test";
        when(userRepository.findByName(username)).thenReturn(null);
        try {
            customUserDetailsService.loadUserByUsername(username);
            fail("Should throw an exception");
        } catch (UsernameNotFoundException e) {
            assertThat("Invalid username or password").isEqualTo(e.getMessage());
        }
    }
}
