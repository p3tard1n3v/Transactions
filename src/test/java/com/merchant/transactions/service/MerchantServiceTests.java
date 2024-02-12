package com.merchant.transactions.service;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.ErrorTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.RefundTransactionEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        long merchantId = 324234L;
        Optional<MerchantEntity> merchantEntityOptional = mock(Optional.class);
        when(merchantRepository.findById(merchantId)).thenReturn(merchantEntityOptional);
        when(merchantEntityOptional.get()).thenReturn(merchantFromRepo);

        MerchantEntity merchant = merchantService.findById(merchantId);

        assertThat(merchant).isEqualTo(merchantFromRepo);
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
        when(merchantDto.getTransactions()).thenReturn(null);

        final MerchantEntity merchantRepo = mock(MerchantEntity.class);
        when(merchantRepository.save(any(MerchantEntity.class))).thenReturn(merchantRepo);

        MerchantEntity merchantFormService = merchantService.save(merchantDto);

        assertThat(merchantFormService).isEqualTo(merchantRepo);
        verify(merchantRepository).save(merchantEntityCaptor.capture());
        MerchantEntity merchantValue = merchantEntityCaptor.getValue();
        assertThat(merchantValue.getName()).isEqualTo(name);
        assertThat(merchantValue.getDescription()).isEqualTo(description);
        assertThat(merchantValue.getEmail()).isEqualTo(email);
        assertThat(merchantValue.getStatus()).isEqualTo(MerchantStatus.ACTIVE);
        assertThat(merchantValue.getTotalTransactionSum()).isEqualTo(BigDecimal.ZERO);
        assertThat(merchantValue.getTransactions()).isNull();
    }

    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    void shouldCalculateTotalSumByAddingApprovedAndsSubtractRefundTransactions() {
        MerchantEntity merchantFromRepo = new MerchantEntity();
        long merchantId = 324234L;
        merchantFromRepo.setId(merchantId);
        Optional<MerchantEntity> merchantEntityOptional = mock(Optional.class);
        when(merchantRepository.findById(merchantId)).thenReturn(merchantEntityOptional);
        when(merchantEntityOptional.get()).thenReturn(merchantFromRepo);
        List<ApprovedTransactionEntity> transactionEntities = new ArrayList<>();
        populateTransactions(transactionEntities);
        BigDecimal sum = calculateTotalSum(transactionEntities);
        when(approvedTransactionRepository.findAllByMerchantId(merchantId)).thenReturn(transactionEntities);
        when(merchantRepository.save(any(MerchantEntity.class))).thenReturn(merchantFromRepo);

        merchantService.updateTotalSum(merchantId);

        verify(merchantRepository).save(merchantEntityCaptor.capture());
        MerchantEntity merchantWithTotalAmount = merchantEntityCaptor.getValue();
        assertThat(merchantWithTotalAmount.getTotalTransactionSum()).isEqualTo(sum);
    }

    @Test
    void testFindDtoById() {
        long merchantId = 3L;
        MerchantEntity merchantEntity = mock(MerchantEntity.class);
        when(merchantEntity.getId()).thenReturn(merchantId);
        when(merchantEntity.getEmail()).thenReturn("test@test.com");
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchantEntity));

        MerchantDto resultDto = merchantService.findDtoById(merchantId);

        assertThat(merchantId).isEqualTo(resultDto.getId());
        assertThat("test@test.com").isEqualTo(resultDto.getEmail());
    }

    @Test
    void testFindByName() {
        String username = "lidl";
        long merchantId = 3L;
        MerchantEntity merchantEntity = mock(MerchantEntity.class);
        when(merchantEntity.getId()).thenReturn(merchantId);
        when(merchantEntity.getEmail()).thenReturn("test@test.com");
        when(merchantRepository.findByName(username)).thenReturn(merchantEntity);

        MerchantDto resultDto = merchantService.findByName(username);

        assertThat(merchantId).isEqualTo(resultDto.getId());
        assertThat("test@test.com").isEqualTo(resultDto.getEmail());
    }

    @Test
    void testEntityByCurrentUser() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username = "lidl";
            long merchantId = 3L;
            MerchantEntity merchantEntity = mock(MerchantEntity.class);
            when(merchantEntity.getId()).thenReturn(merchantId);
            when(merchantEntity.getEmail()).thenReturn("test@test.com");
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username);

            when(merchantRepository.findByName(username)).thenReturn(merchantEntity);

            MerchantEntity result = merchantService.findEntityByCurrentUser();

            assertThat(merchantId).isEqualTo(result.getId());
            assertThat("test@test.com").isEqualTo(result.getEmail());
        }
    }

    @Test
    void shouldIsAuthorisedReturnTrueWhenUserIsAdmin() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username1 = "lidl";
            String username2 = "bila";
            long merchantId = 3L;
            MerchantEntity merchantEntity = mock(MerchantEntity.class);
            when(merchantEntity.getId()).thenReturn(merchantId);
            when(merchantEntity.getName()).thenReturn(username1);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username2);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(true);
            when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchantEntity));

            boolean result = merchantService.isAuthorized(merchantId);

            assertThat(true).isEqualTo(result);
        }
    }

    @Test
    void shouldIsAuthorisedReturnTrueWhenUserIsNotAdminButUserNameDoesNotMatches() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username1 = "lidl";
            String username2 = "bila";
            long merchantId = 3L;
            MerchantEntity merchantEntity = mock(MerchantEntity.class);
            when(merchantEntity.getId()).thenReturn(merchantId);
            when(merchantEntity.getName()).thenReturn(username1);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username2);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(false);

            when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchantEntity));

            boolean result = merchantService.isAuthorized(merchantId);

            assertThat(false).isEqualTo(result);
        }
    }



    @Test
    void shouldIsAuthorisedReturnFalseWhenUserIsNotAdminAndUsernameMatches() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username = "lidl";
            long merchantId = 3L;
            MerchantEntity merchantEntity = mock(MerchantEntity.class);
            when(merchantEntity.getId()).thenReturn(merchantId);
            when(merchantEntity.getName()).thenReturn(username);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(false);

            when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchantEntity));

            boolean result = merchantService.isAuthorized(merchantId);

            assertThat(true).isEqualTo(result);
        }
    }

    @Test
    void shouldIsAuthorisedFromDtoReturnTrueWhenUserIsAdmin() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username1 = "lidl";
            String username2 = "bila";
            MerchantDto merchantDto = mock(MerchantDto.class);
            lenient().when(merchantDto.getName()).thenReturn(username1);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username2);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(true);

            boolean result = merchantService.isAuthorized(merchantDto);

            assertThat(true).isEqualTo(result);
        }
    }

    @Test
    void shouldIsAuthorisedFromDtoReturnTrueWhenUserIsNotAdminButUserNameDoesNotMatch() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username1 = "lidl";
            String username2 = "bila";
            MerchantDto merchantDto = mock(MerchantDto.class);
            when(merchantDto.getName()).thenReturn(username1);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username2);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(false);

            boolean result = merchantService.isAuthorized(merchantDto);

            assertThat(false).isEqualTo(result);
        }
    }



    @Test
    void shouldIsAuthorisedFromDtoReturnFalseWhenUserIsNotAdminAndUsernameMatches() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username = "lidl";
            MerchantDto merchantDto = mock(MerchantDto.class);
            when(merchantDto.getName()).thenReturn(username);
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(false);

            boolean result = merchantService.isAuthorized(merchantDto);

            assertThat(true).isEqualTo(result);
        }
    }

    @Test
    void testFindAll() {
        long merchantId1 = 3L;
        MerchantEntity merchantEntity1 = mock(MerchantEntity.class);
        when(merchantEntity1.getId()).thenReturn(merchantId1);
        when(merchantEntity1.getEmail()).thenReturn("test@test.com");

        long merchantId2 = 7L;
        MerchantEntity merchantEntity2 = mock(MerchantEntity.class);
        when(merchantEntity2.getId()).thenReturn(merchantId2);
        when(merchantEntity2.getEmail()).thenReturn("bila@lidl1.com");

        List<MerchantEntity> merchantEntities = List.of(merchantEntity1, merchantEntity2);
        when(merchantRepository.findAll()).thenReturn(merchantEntities);

        List<MerchantDto> resultDto = merchantService.findAll();

        assertThat(3L).isEqualTo(resultDto.get(0).getId());
        assertThat("test@test.com").isEqualTo(resultDto.get(0).getEmail());

        assertThat(7L).isEqualTo(resultDto.get(1).getId());
        assertThat("bila@lidl1.com").isEqualTo(resultDto.get(1).getEmail());
    }

    @Test
    void testTransactionsCountById() {
        long merchantId = 3L;
        when(approvedTransactionRepository.countAllByMerchantId(merchantId)).thenReturn(151);

        int result = merchantService.transactionsCountById(merchantId);

        assertThat(151).isEqualTo(result);
    }

    @Test
    void testDelete() {
        long merchantId = 3L;

        merchantService.delete(merchantId);

        verify(merchantRepository).deleteById(merchantId);
    }


    @Test
    void testUpdateMerchant() {
        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantDto.getId()).thenReturn(100L);
        when(merchantDto.getStatus()).thenReturn(MerchantStatus.INACTIVE);
        when(merchantDto.getName()).thenReturn("Lidl");
        when(merchantDto.getEmail()).thenReturn("test@test.com");
        when(merchantDto.getDescription()).thenReturn("desc");
        when(merchantDto.getTotalTransactionSum()).thenReturn(BigDecimal.TEN);
        when(merchantRepository.findById(merchantDto.getId()))
                .thenReturn(Optional.of(mock(MerchantEntity.class)));

        merchantService.updateMerchant(merchantDto);

        verify(merchantRepository).save(merchantEntityCaptor.capture());
        MerchantEntity merchantValue = merchantEntityCaptor.getValue();
        assertThat(MerchantStatus.INACTIVE).isEqualTo(merchantDto.getStatus());
        assertThat("Lidl").isEqualTo(merchantDto.getName());
        assertThat("test@test.com").isEqualTo(merchantDto.getEmail());
        assertThat("desc").isEqualTo(merchantDto.getDescription());
        assertThat(BigDecimal.TEN).isEqualTo(merchantDto.getTotalTransactionSum());
    }

    private BigDecimal calculateTotalSum(List<ApprovedTransactionEntity> transactionEntities) {
        BigDecimal sum = BigDecimal.ZERO;
        for (var trans : transactionEntities) {
            if (trans.getClass().equals(ApprovedTransactionEntity.class)) {
                sum = sum.add(trans.getAmount());
            } else if (trans.getClass().equals(RefundTransactionEntity.class)) {
                sum = sum.subtract(trans.getAmount());
            }
        }

        return sum;
    }

    private void populateTransactions(List<ApprovedTransactionEntity> transactionEntities) {
        transactionEntities.add(createTransaction(ApprovedTransactionEntity.class, BigDecimal.ONE));
        transactionEntities.add(createTransaction(ApprovedTransactionEntity.class, BigDecimal.TEN));
        transactionEntities.add(createTransaction(RefundTransactionEntity.class, new BigDecimal(2)));
        transactionEntities.add(createTransaction(RefundTransactionEntity.class, BigDecimal.ONE));
        transactionEntities.add(createTransaction(ErrorTransactionEntity.class, BigDecimal.ONE));
    }

    private ApprovedTransactionEntity createTransaction(Class<? extends ApprovedTransactionEntity> transClass, BigDecimal amount) {
        ApprovedTransactionEntity tran = mock(transClass);
        when(tran.getAmount()).thenReturn(amount);

        return tran;
    }
}
