package com.merchant.transactions.service;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.service.impl.MerchantServiceImpl;
import com.merchant.transactions.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceTests {
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private ApprovedTransactionRepository approvedTransactionRepository;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Captor
    ArgumentCaptor<MerchantEntity> merchantEntityCaptor;

    @Test
    public void shouldFindByIdReturnFindByIdFromRepository() {
        MerchantEntity merchantFromRepo = mock(MerchantEntity.class);
        long merchantId = 324234l;
        Optional<MerchantEntity> merchantEntityOptional = mock(Optional.class);
        when(merchantRepository.findById(merchantId)).thenReturn(merchantEntityOptional);
        when(merchantEntityOptional.get()).thenReturn(merchantFromRepo);

        MerchantEntity merchant = merchantService.findById(merchantId);

        Assertions.assertThat(merchant).isEqualTo(merchantFromRepo);
    }


    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    public void shouldSaveFromDtoReturnEntityFromRepository() {
        MerchantDto merchantDto = mock(MerchantDto.class);
        String name = "asdasd";
        when(merchantDto.getName()).thenReturn(name);
        String description = "description";
        when(merchantDto.getDescription()).thenReturn(description);
        String email = "email";
        when(merchantDto.getEmail()).thenReturn(email);
        when(merchantDto.getStatus()).thenReturn(MerchantStatus.ACTIVE);
        when(merchantDto.getTotalTransactionSum()).thenReturn(BigDecimal.ZERO);
        UserEntity user = mock(UserEntity.class);
        when(merchantDto.getUser()).thenReturn(user);
        when(merchantDto.getTransactions()).thenReturn(null);

        final MerchantEntity merchantRepo = mock(MerchantEntity.class);
        when(merchantRepository.save(any(MerchantEntity.class))).thenReturn(merchantRepo);

        MerchantEntity merchantFormService = merchantService.save(merchantDto);

        Assertions.assertThat(merchantFormService).isEqualTo(merchantRepo);
        verify(merchantRepository).save(merchantEntityCaptor.capture());
        MerchantEntity merchantValue = merchantEntityCaptor.getValue();
        Assertions.assertThat(merchantValue.getName()).isEqualTo(name);
        Assertions.assertThat(merchantValue.getDescription()).isEqualTo(description);
        Assertions.assertThat(merchantValue.getEmail()).isEqualTo(email);
        Assertions.assertThat(merchantValue.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        Assertions.assertThat(merchantValue.getTotalTransactionSum()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(merchantValue.getUser()).isEqualTo(user);
        Assertions.assertThat(merchantValue.getTransactions()).isNull();
    }

    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    void shouldCalculateTotalSumByAddingApprovedAndsSubtractRefundTransactions() {
        MerchantEntity merchantFromRepo = new MerchantEntity();
        long merchantId = 324234l;
        merchantFromRepo.setId(merchantId);
        Optional<MerchantEntity> merchantEntityOptional = mock(Optional.class);
        when(merchantRepository.findById(merchantId)).thenReturn(merchantEntityOptional);
        when(merchantEntityOptional.get()).thenReturn(merchantFromRepo);
        List<ApprovedTransactionEntity> transactionEntities = new ArrayList<>();
        populateTransactions(transactionEntities);
        BigDecimal sum = calculateTotalSum(transactionEntities);
        when(approvedTransactionRepository.findByMerchantId(merchantId)).thenReturn(transactionEntities);
        when(merchantRepository.save(any(MerchantEntity.class))).thenReturn(merchantFromRepo);

        merchantService.updateTotalSum(merchantId);

        verify(merchantRepository).save(merchantEntityCaptor.capture());
        MerchantEntity merchantWithTotalAmount = merchantEntityCaptor.getValue();
        Assertions.assertThat(merchantWithTotalAmount.getTotalTransactionSum()).isEqualTo(sum);

    }

    private BigDecimal calculateTotalSum(List<ApprovedTransactionEntity> transactionEntities) {
        BigDecimal sum = BigDecimal.ZERO;
        for (var trans : transactionEntities) {
            if (trans.getStatus().equals(TransactionStatus.APPROVED)) {
                sum = sum.add(trans.getAmount());
            } else if (trans.getStatus().equals(TransactionStatus.REFUNDED)) {
                sum = sum.subtract(trans.getAmount());
            }
        }

        return sum;
    }

    private void populateTransactions(List<ApprovedTransactionEntity> transactionEntities) {
        transactionEntities.add(createTransaction(TransactionStatus.APPROVED, BigDecimal.ONE));
        transactionEntities.add(createTransaction(TransactionStatus.APPROVED, BigDecimal.TEN));
        transactionEntities.add(createTransaction(TransactionStatus.REFUNDED, new BigDecimal(2)));
        transactionEntities.add(createTransaction(TransactionStatus.REFUNDED, BigDecimal.ONE));
        transactionEntities.add(createTransaction(TransactionStatus.ERROR, BigDecimal.ONE));
    }

    private ApprovedTransactionEntity createTransaction(TransactionStatus status, BigDecimal amount) {
        ApprovedTransactionEntity tran = mock(ApprovedTransactionEntity.class);
        when(tran.getStatus()).thenReturn(status);
        when(tran.getAmount()).thenReturn(amount);

        return tran;
    }
}
