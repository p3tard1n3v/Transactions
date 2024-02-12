package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionRestApiControllerTests {

    @Mock
    private MerchantService merchantService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    TransactionRestApiController transactionRestApiController;

    @Test
    public void testDoesNotCreateTransactionWhenMerchantIsInactive() {
        AuthorizeTransactionDto authorizeTransactionDto = mock(AuthorizeTransactionDto.class);
        MerchantEntity merchantEntity = mock(MerchantEntity.class);
        when(merchantEntity.getStatus()).thenReturn(MerchantStatus.INACTIVE);
        when(merchantService.findEntityByCurrentUser()).thenReturn(merchantEntity);


        ResponseEntity<String> result = transactionRestApiController
                .createTransaction(authorizeTransactionDto);

        assertThat(HttpStatus.UNAUTHORIZED.value()).isEqualTo(result.getStatusCode().value());
        assertThat("Merchant is inactive. Transaction cannot be create").isEqualTo(result.getBody());
        verifyNoInteractions(transactionService);
    }

    @Test
    public void testCreateTransactionSuccessfullyWhenMerchantIsActive() {
        AuthorizeTransactionDto authorizeTransactionDto = mock(AuthorizeTransactionDto.class);
        MerchantEntity merchantEntity = mock(MerchantEntity.class);
        when(merchantEntity.getStatus()).thenReturn(MerchantStatus.ACTIVE);
        when(merchantService.findEntityByCurrentUser()).thenReturn(merchantEntity);


        ResponseEntity<String> result = transactionRestApiController
                .createTransaction(authorizeTransactionDto);

        assertThat(HttpStatus.CREATED.value()).isEqualTo(result.getStatusCode().value());
        assertThat("Transaction create").isEqualTo(result.getBody());
        verify(transactionService).save(authorizeTransactionDto, merchantEntity);
    }
}
