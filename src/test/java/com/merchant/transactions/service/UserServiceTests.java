package com.merchant.transactions.service;

import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.repository.UserRepository;
import com.merchant.transactions.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    ArgumentCaptor<UserEntity> userEntityCaptor;

    @Test
    public void shouldUserCountReturnCountFromRepository() {
        long countRepository = 232343L;
        when(userRepository.count()).thenReturn(countRepository);

        long countFromService = userService.usersCount();

        Assertions.assertThat(countFromService).isEqualTo(countRepository);
    }

    @Test
    public void shouldFindByUsernameReturnFindByUsernameFromRepository() {
        String username = "Gosho";
        UserEntity user = mock(UserEntity.class);
        when(userRepository.findByName(username)).thenReturn(user);

        UserEntity userFromService = userService.findByUsername(username);

        Assertions.assertThat(userFromService).isEqualTo(user);
    }

    @Test
    public void shouldSaveUserFromDtoObjectAndReturnEntityFromRepository() {
        final UserDto userDto = mock(UserDto.class);
        UserEntity userReturn = mock(UserEntity.class);
        when(userDto.getName()).thenReturn("test1");
        when(userDto.getPassword()).thenReturn("test1");
        String hashPass = "asdasd345345!sdfsdf";
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(hashPass);
        lenient().when(userRepository.save(any(UserEntity.class))).thenReturn(userReturn);

        UserEntity userFromService = userService.save(userDto);

        Assertions.assertThat(userFromService).isEqualTo(userReturn);
        verify(userRepository).save(userEntityCaptor.capture());
        UserEntity valueUser = userEntityCaptor.getValue();
        Assertions.assertThat(valueUser.getName()).isEqualTo(userDto.getName());
        Assertions.assertThat(valueUser.getPassword()).isEqualTo(hashPass);
    }


}
