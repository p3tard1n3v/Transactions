package com.merchant.transactions.service;

import com.merchant.transactions.dto.ApprovedTransactionDto;
import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.*;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.AuthorizeTransactionRepository;
import com.merchant.transactions.repository.ErrorTransactionRepository;
import com.merchant.transactions.repository.ReversalTransactionRepository;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {
    private static final UUID UUID_TRANSACTION = UUID.randomUUID();
    private static final String EMAIL = "email@email.com";
    private static final String PHONE = "+359888345678";
    private static final UUID REFERENCE = UUID.randomUUID();

    private static final MerchantEntity MERCHANT_MOCK = mock(MerchantEntity.class);
    @Mock
    private ApprovedTransactionRepository approvedTransactionRepository;
    @Mock
    private AuthorizeTransactionRepository authorizeTransactionRepository;
    @Mock
    private ReversalTransactionRepository reversalTransactionRepository;
    @Mock
    private ErrorTransactionRepository errorTransactionRepository;
    @Mock
    private MerchantService merchantService;
    @InjectMocks
    private TransactionServiceImpl authorizeAuthorizeTransactionService;
    @Captor
    ArgumentCaptor<AuthorizeTransactionEntity> authorizeTransactionEntityCaptor;
    @Captor
    ArgumentCaptor<ReversalTransactionEntity> reversalTransactionEntityCaptor;
    @Captor
    ArgumentCaptor<ErrorTransactionEntity> errorTransactionEntityCaptor;

    @Captor
    ArgumentCaptor<LocalDateTime> localDateTimeArgumentCaptor;

    @Test
    public void shouldSaveErrorTransactionWhenReferenceIsNotNullFromTransactionDto() {
        final BigDecimal amount = BigDecimal.TEN;
        final AuthorizeTransactionDto transactionDto = getAuthorizeTransactionDto(REFERENCE, amount);

        final ErrorTransactionEntity errorTransactionEntity = mock(ErrorTransactionEntity.class);
        when(errorTransactionRepository.save(any(ErrorTransactionEntity.class))).thenReturn(errorTransactionEntity);

        AuthorizeTransactionEntity result = authorizeAuthorizeTransactionService.save(transactionDto);

        assertThat(result).isEqualTo(errorTransactionEntity);
        verify(errorTransactionRepository).save(errorTransactionEntityCaptor.capture());
        final AuthorizeTransactionEntity transactionCaptured = errorTransactionEntityCaptor.getValue();
        compareProperties(REFERENCE, amount, transactionCaptured);

        assertThat(result.getClass()).isEqualTo(ErrorTransactionEntity.class);
    }

    @Test
    public void shouldSaveReversedTransactionWhenReferenceIsNotNullFromTransactionDto() {
        final BigDecimal amount = BigDecimal.ZERO;
        final AuthorizeTransactionDto transactionDto = getAuthorizeTransactionDto(null, amount);

        final ReversalTransactionEntity reversalTransactionEntity = mock(ReversalTransactionEntity.class);
        when(reversalTransactionRepository.save(any(ReversalTransactionEntity.class))).thenReturn(reversalTransactionEntity);

        AuthorizeTransactionEntity result = authorizeAuthorizeTransactionService.save(transactionDto);

        assertThat(result).isEqualTo(reversalTransactionEntity);
        verify(reversalTransactionRepository).save(reversalTransactionEntityCaptor.capture());
        final AuthorizeTransactionEntity transactionCaptured = reversalTransactionEntityCaptor.getValue();
        compareProperties(null, amount, transactionCaptured);

        assertThat(result.getClass()).isEqualTo(ReversalTransactionEntity.class);
    }

    @Test
    public void shouldSaveAuthoriseTransactionWhenReferenceIsNotNullFromTransactionDto() {
        final BigDecimal amount = BigDecimal.ONE;
        final AuthorizeTransactionDto transactionDto = getAuthorizeTransactionDto(null, amount);

        final AuthorizeTransactionEntity authorizeTransactionEntity = mock(AuthorizeTransactionEntity.class);
        when(authorizeTransactionRepository.save(any(AuthorizeTransactionEntity.class))).thenReturn(authorizeTransactionEntity);

        AuthorizeTransactionEntity result = authorizeAuthorizeTransactionService.save(transactionDto);

        assertThat(result).isEqualTo(authorizeTransactionEntity);
        verify(authorizeTransactionRepository).save(authorizeTransactionEntityCaptor.capture());
        final AuthorizeTransactionEntity transactionCaptured = authorizeTransactionEntityCaptor.getValue();
        compareProperties(null, amount, transactionCaptured);

        assertThat(result.getClass()).isEqualTo(AuthorizeTransactionEntity.class);
    }

    @Test
    public void shouldSaveAuthoriseTransactionWhenReferenceIsNotNullAndSumIsNotZeroFromTransactionDto() {
        final BigDecimal amount = BigDecimal.ONE;
        final AuthorizeTransactionDto transactionDto = getAuthorizeTransactionDto(null, amount);
        MerchantEntity merchantEntity = mock(MerchantEntity.class);

        final AuthorizeTransactionEntity authorizeTransactionEntity = mock(AuthorizeTransactionEntity.class);
        when(authorizeTransactionRepository.save(any(AuthorizeTransactionEntity.class))).thenReturn(authorizeTransactionEntity);

        authorizeAuthorizeTransactionService.save(transactionDto, merchantEntity);

        verify(transactionDto).setMerchant(merchantEntity);
        verify(authorizeTransactionRepository).save(authorizeTransactionEntityCaptor.capture());
        final AuthorizeTransactionEntity transactionCaptured = authorizeTransactionEntityCaptor.getValue();
        compareProperties(null, amount, transactionCaptured);

        assertThat(transactionCaptured.getClass()).isEqualTo(AuthorizeTransactionEntity.class);
    }

    @Test
    public void testDeleteOldThanOneHourTransactions() {
        LocalDateTime localDateTime = LocalDateTime.now();
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {

            mockedStatic.when(LocalDateTime::now).thenReturn(localDateTime);
            authorizeAuthorizeTransactionService.deleteOldThanOneHourTransactions();

            verify(approvedTransactionRepository).deleteAllByLastUpdatedLessThan(localDateTimeArgumentCaptor.capture());
            assertThat(LocalDate.from(localDateTime)).isEqualTo(LocalDate.from(localDateTimeArgumentCaptor.getValue()));
        }
    }

    @Test
    public void testFindByMerchant() {
        Long merchantId = 100L;

        MerchantEntity merchant = mock(MerchantEntity.class);
        when(merchantService.findById(merchantId)).thenReturn(merchant);
        List<AuthorizeTransactionEntity> authorizeTransactionEntities = createAuthorizeTransactionEntities();
        List<ApprovedTransactionEntity> approvedTransactionEntities = createApprovedTransactionEntities(authorizeTransactionEntities);
        when(approvedTransactionRepository.findAllByMerchantId(merchantId)).thenReturn(approvedTransactionEntities);
        when(authorizeTransactionRepository.findAllByMerchant(merchant)).thenReturn(authorizeTransactionEntities);


        List<AuthorizeTransactionDto> result = authorizeAuthorizeTransactionService.findByMerchant(merchantId);

        assertThat(true).isEqualTo(result
                .stream()
                .allMatch(el-> el.getApprovedReferenceBy() == null || el.getApprovedReferenceBy().stream().allMatch(approved -> canBeReferenced(approved))));
        assertThat(5).isEqualTo(result.size());

        assertThat(result.get(0).getApprovedReferenceBy()).isNull();
        assertThat(2).isEqualTo(result.get(1).getApprovedReferenceBy().size());
        assertThat(1).isEqualTo(result.get(2).getApprovedReferenceBy().size());
        assertThat(result.get(3).getApprovedReferenceBy()).isNull();
        assertThat(result.get(4).getApprovedReferenceBy()).isNull();

    }

    private boolean canBeReferenced(ApprovedTransactionDto approved) {
        return approved.getStatus().equals(TransactionStatus.APPROVED)
                || approved.getStatus().equals(TransactionStatus.REFUNDED);
    }

    private List<ApprovedTransactionEntity> createApprovedTransactionEntities(List<AuthorizeTransactionEntity> authorizeTransactionEntities) {
        List<ApprovedTransactionEntity> approvedTransactionEntities = new ArrayList<>(authorizeTransactionEntities);
        AuthorizeTransactionEntity authorizeTransaction2 =
                (AuthorizeTransactionEntity) approvedTransactionEntities.get(1);
        AuthorizeTransactionEntity authorizeTransaction3 =
                (AuthorizeTransactionEntity) approvedTransactionEntities.get(2);

        ApprovedTransactionEntity transaction1 = ApprovedTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(.23))
                .reference(authorizeTransaction2.getId())
                .build();

        ApprovedTransactionEntity transaction2 = ApprovedTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(33333.23))
                .reference(authorizeTransaction3.getId())
                .build();

        RefundTransactionEntity transaction3 = RefundTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(.23))
                .reference(authorizeTransaction2.getId())
                .build();

        approvedTransactionEntities.add(transaction1);
        approvedTransactionEntities.add(transaction2);
        approvedTransactionEntities.add(transaction3);

        return approvedTransactionEntities;
    }

    private List<AuthorizeTransactionEntity> createAuthorizeTransactionEntities() {
        AuthorizeTransactionEntity transaction1 = AuthorizeTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(100.23))
                .customerEmail("test1@transaction.com")
                .customerPhone("+359888345678")
                .build();

        AuthorizeTransactionEntity transaction2 = AuthorizeTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(.23))
                .customerEmail("test2@transaction.com")
                .customerPhone("+359888111345")
                .build();

        AuthorizeTransactionEntity transaction3 = AuthorizeTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(33333.23))
                .customerEmail("test3@transaction.com")
                .customerPhone("+359888111000")
                .build();

        ReversalTransactionEntity transaction4 = ReversalTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .customerEmail("test4@transaction.com")
                .customerPhone("+359888111300")
                .build();

        ErrorTransactionEntity transaction5 = ErrorTransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal(300.3))
                .customerEmail("test5@transaction.com")
                .customerPhone("+359888111300")
                .reference(transaction1.getId())
                .build();

        return List.of(transaction1, transaction2, transaction3, transaction4, transaction5);
    }

    private static void compareProperties(UUID reference, BigDecimal amount, AuthorizeTransactionEntity transactionCaptured) {
        assertThat(transactionCaptured.getId()).isEqualTo(UUID_TRANSACTION);
        assertThat(transactionCaptured.getAmount()).isEqualTo(amount);
        assertThat(transactionCaptured.getCustomerEmail()).isEqualTo(EMAIL);
        assertThat(transactionCaptured.getCustomerPhone()).isEqualTo(PHONE);
        assertThat(transactionCaptured.getReference()).isEqualTo(reference);
        assertThat(transactionCaptured.getMerchant()).isEqualTo(MERCHANT_MOCK);
    }

    private AuthorizeTransactionDto getAuthorizeTransactionDto(UUID reference, BigDecimal amount) {
        final AuthorizeTransactionDto transactionDto = mock(AuthorizeTransactionDto.class);
        when(transactionDto.getId()).thenReturn(UUID_TRANSACTION);
        when(transactionDto.getAmount()).thenReturn(amount);
        when(transactionDto.getCustomerEmail()).thenReturn(EMAIL);
        when(transactionDto.getCustomerPhone()).thenReturn(PHONE);
        when(transactionDto.getReference()).thenReturn(reference);
        when(transactionDto.getMerchant()).thenReturn(MERCHANT_MOCK);
        return transactionDto;
    }

}
