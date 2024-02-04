package com.merchant.transactions.service;

import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.service.impl.ChargeAndRefundServiceImpl;
import com.merchant.transactions.service.impl.NotAllowedOperationRefundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChargeAndRefundServiceTests {
    @Mock
    private ApprovedTransactionRepository approvedTransactionRepository;
    @Mock
    private MerchantService merchantService;
    @InjectMocks
    private ChargeAndRefundServiceImpl chargeAndRefundService;

    @Captor
    ArgumentCaptor<ApprovedTransactionEntity> approvedTransactionEntityCaptor;

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
        assertThat(transactionValue.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    public void shouldCreateNewRefundTransactionRefreshMerchantTotalSumWhenRefundIsCalled() throws NotAllowedOperationRefundException {
        ApprovedTransactionEntity approvedTransaction = mock(ApprovedTransactionEntity.class);
        when(approvedTransaction.getAmount()).thenReturn(BigDecimal.TEN);
        UUID uuid = UUID.randomUUID();
        when(approvedTransaction.getReference()).thenReturn(uuid);
        MerchantEntity merchantMock = mock(MerchantEntity.class);
        long merchantId = 1234545l;
        when(merchantMock.getId()).thenReturn(merchantId);
        when(approvedTransaction.getMerchant()).thenReturn(merchantMock);
        when(approvedTransaction.getStatus()).thenReturn(TransactionStatus.APPROVED);

        ApprovedTransactionEntity transactionRepo = mock(ApprovedTransactionEntity.class);
        when(approvedTransactionRepository.saveAndFlush(any(ApprovedTransactionEntity.class))).thenReturn(transactionRepo);
        doNothing().when(merchantService).updateTotalSum(merchantId);

        ApprovedTransactionEntity refundedTransaction = chargeAndRefundService.refund(approvedTransaction);

        assertThat(refundedTransaction).isEqualTo(transactionRepo);

        verify(approvedTransactionRepository).saveAndFlush(approvedTransactionEntityCaptor.capture());
        ApprovedTransactionEntity transactionValue = approvedTransactionEntityCaptor.getValue();
        assertThat(transactionValue.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(transactionValue.getMerchant()).isEqualTo(merchantMock);
        assertThat(transactionValue.getReference()).isEqualTo(uuid);
        assertThat(transactionValue.getStatus()).isEqualTo(TransactionStatus.REFUNDED);
    }

    @Test
    public void shouldThrowExceptionWhenRefundTransactionFromAnyOtherTypeThanApproved() {
        List<TransactionStatus> nonApprovedStatus = Arrays.stream(TransactionStatus.values())
                .filter(s -> !s.equals(TransactionStatus.APPROVED)).collect(Collectors.toList());
        nonApprovedStatus.add(null);
        
        for (var status: nonApprovedStatus) {
            testForNonApprovedStatuses(status);
        }
    }

    private void testForNonApprovedStatuses(TransactionStatus status) {
        ApprovedTransactionEntity approvedTransaction = mock(ApprovedTransactionEntity.class);
        when(approvedTransaction.getStatus()).thenReturn(status);

        Exception exception = assertThrows(NotAllowedOperationRefundException.class, () -> {
            chargeAndRefundService.refund(approvedTransaction);
        });

        assertThat(exception.getMessage())
                .isEqualTo("refund operation cannot be execute on transaction that is not in status="
                        + TransactionStatus.APPROVED);
    }
}
