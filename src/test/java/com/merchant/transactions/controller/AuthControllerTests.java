package com.merchant.transactions.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @InjectMocks
    private AuthController authController;

    @Test
    public void testAuthPage() throws Exception {
        assertThat("login").isEqualTo(authController.authPage());
    }

}
