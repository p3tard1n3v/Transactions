package com.merchant.transactions.service;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.AuthorizeTransactionRepository;
import com.merchant.transactions.service.impl.AuthorizeAuthorizeTransactionServiceImpl;
import org.junit.jupiter.api.IndicativeSentencesGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizeAuthorizeTransactionServiceTests {
    private static final UUID UUID_TRANSACTION = UUID.randomUUID();
    private static final String EMAIL = "email@email.com";
    private static final String PHONE = "+359888345678";
    private static final UUID REFERENCE = UUID.randomUUID();

    private static final MerchantEntity MERCHANT_MOCK = mock(MerchantEntity.class);
    @Mock
    private AuthorizeTransactionRepository authorizeTransactionRepository;
    @InjectMocks
    private AuthorizeAuthorizeTransactionServiceImpl authorizeAuthorizeTransactionService;
    @Captor
    ArgumentCaptor<AuthorizeTransactionEntity> authorizeTransactionEntityCaptor;

    @Test
    public void shouldSaveWithStatusErrorWhenReferenceIsNotNullFromTransactionDto() {
        testSaveAuthorizeTransactionEntity(REFERENCE, BigDecimal.ONE, TransactionStatus.ERROR);
    }

    @Test
    public void shouldSaveWithStatusReversedWhenReferenceIsNotNullFromTransactionDto() {
        testSaveAuthorizeTransactionEntity(null, BigDecimal.ZERO, TransactionStatus.REVERSED);
    }

    private void testSaveAuthorizeTransactionEntity(final UUID reference, final BigDecimal amount,
                                                    final TransactionStatus status) {
        final AuthorizeTransactionDto transactionDto = mock(AuthorizeTransactionDto.class);
        when(transactionDto.getId()).thenReturn(UUID_TRANSACTION);
        when(transactionDto.getAmount()).thenReturn(amount);
        when(transactionDto.getStatus()).thenReturn(null);
        when(transactionDto.getCustomerEmail()).thenReturn(EMAIL);
        when(transactionDto.getCustomerPhone()).thenReturn(PHONE);
        when(transactionDto.getReference()).thenReturn(reference);
        when(transactionDto.getMerchant()).thenReturn(MERCHANT_MOCK);

        final AuthorizeTransactionEntity authorizeTransactionEntity = mock(AuthorizeTransactionEntity.class);
        when(authorizeTransactionRepository.save(any(AuthorizeTransactionEntity.class))).thenReturn(authorizeTransactionEntity);

        AuthorizeTransactionEntity result = authorizeAuthorizeTransactionService.save(transactionDto);

        assertThat(result).isEqualTo(authorizeTransactionEntity);
        verify(authorizeTransactionRepository).save(authorizeTransactionEntityCaptor.capture());
        final AuthorizeTransactionEntity transactionCaptured = authorizeTransactionEntityCaptor.getValue();
        assertThat(transactionCaptured.getStatus()).isEqualTo(status);
        assertThat(transactionCaptured.getId()).isEqualTo(UUID_TRANSACTION);
        assertThat(transactionCaptured.getAmount()).isEqualTo(amount);
        assertThat(transactionCaptured.getCustomerEmail()).isEqualTo(EMAIL);
        assertThat(transactionCaptured.getCustomerPhone()).isEqualTo(PHONE);
        assertThat(transactionCaptured.getReference()).isEqualTo(reference);
        assertThat(transactionCaptured.getMerchant()).isEqualTo(MERCHANT_MOCK);
    }

}
