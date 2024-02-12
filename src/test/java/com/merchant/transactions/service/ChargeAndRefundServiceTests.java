package com.merchant.transactions.service;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.RefundTransactionEntity;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.RefundTransactionRepository;
import com.merchant.transactions.service.impl.ChargeAndRefundServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChargeAndRefundServiceTests {
    @Mock
    private ApprovedTransactionRepository approvedTransactionRepository;
    @Mock
    private RefundTransactionRepository refundTransactionRepository;
    @Mock
    private MerchantService merchantService;
    @InjectMocks
    private ChargeAndRefundServiceImpl chargeAndRefundService;

    @Captor
    ArgumentCaptor<ApprovedTransactionEntity> approvedTransactionEntityCaptor;

    @Captor
    ArgumentCaptor<RefundTransactionEntity> refundTransactionEntityCaptor;

    @Test
    public void shouldCreateNewApprovedTransactionRefreshMerchantTotalSumWhenApprovedIsCalled() {
        AuthorizeTransactionEntity authorizeTransaction = mock(AuthorizeTransactionEntity.class);
        when(authorizeTransaction.getAmount()).thenReturn(BigDecimal.TEN);
        UUID uuid = UUID.randomUUID();
        when(authorizeTransaction.getId()).thenReturn(uuid);
        MerchantEntity merchantMock = mock(MerchantEntity.class);
        long merchantId = 1234545l;
        when(merchantMock.getId()).thenReturn(merchantId);
        when(authorizeTransaction.getMerchant()).thenReturn(merchantMock);

        ApprovedTransactionEntity transactionRepo = mock(ApprovedTransactionEntity.class);
        when(approvedTransactionRepository.saveAndFlush(any(ApprovedTransactionEntity.class))).thenReturn(transactionRepo);
        doNothing().when(merchantService).updateTotalSum(merchantId);

        ApprovedTransactionEntity approvedTransaction = chargeAndRefundService.approve(authorizeTransaction);

        assertThat(approvedTransaction).isEqualTo(transactionRepo);

        verify(approvedTransactionRepository).saveAndFlush(approvedTransactionEntityCaptor.capture());
        ApprovedTransactionEntity transactionValue = approvedTransactionEntityCaptor.getValue();
        assertThat(transactionValue.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(transactionValue.getMerchant()).isEqualTo(merchantMock);
        assertThat(transactionValue.getReference()).isEqualTo(uuid);
    }

    @Test
    public void shouldCreateNewRefundTransactionRefreshMerchantTotalSumWhenRefundIsCalled() {
        ApprovedTransactionEntity approvedTransaction = mock(ApprovedTransactionEntity.class);
        when(approvedTransaction.getAmount()).thenReturn(BigDecimal.TEN);
        UUID uuid = UUID.randomUUID();
        when(approvedTransaction.getReference()).thenReturn(uuid);
        MerchantEntity merchantMock = mock(MerchantEntity.class);
        long merchantId = 1234545l;
        when(merchantMock.getId()).thenReturn(merchantId);
        when(approvedTransaction.getMerchant()).thenReturn(merchantMock);

        RefundTransactionEntity transactionRepo = mock(RefundTransactionEntity.class);
        when(refundTransactionRepository.saveAndFlush(any(RefundTransactionEntity.class))).thenReturn(transactionRepo);
        doNothing().when(merchantService).updateTotalSum(merchantId);

        RefundTransactionEntity refundedTransaction = chargeAndRefundService.refund(approvedTransaction);

        assertThat(refundedTransaction).isEqualTo(transactionRepo);

        verify(refundTransactionRepository).saveAndFlush(refundTransactionEntityCaptor.capture());
        ApprovedTransactionEntity transactionValue = refundTransactionEntityCaptor.getValue();
        assertThat(transactionValue.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(transactionValue.getMerchant()).isEqualTo(merchantMock);
        assertThat(transactionValue.getReference()).isEqualTo(uuid);
    }
}
