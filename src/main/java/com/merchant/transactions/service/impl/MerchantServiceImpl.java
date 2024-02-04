package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.mapper.MerchantMapper;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.service.MerchantService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MerchantServiceImpl implements MerchantService {
    static final Set<TransactionStatus> APPROVED_STATUS = Stream.of(TransactionStatus.APPROVED, TransactionStatus.REFUNDED)
            .collect(Collectors.toUnmodifiableSet());
    private MerchantRepository merchantRepository;
    private ApprovedTransactionRepository approvedTransactionRepository;

    @Autowired
    public MerchantServiceImpl(final MerchantRepository merchantRepository,
                               final ApprovedTransactionRepository approvedTransactionRepository) {
        this.merchantRepository = merchantRepository;
        this.approvedTransactionRepository = approvedTransactionRepository;
    }

    @Override
    public MerchantEntity findById(Long merchantId) {
        return merchantRepository.findById(merchantId).get();
    }

    @Override
    public MerchantEntity save(MerchantDto merchantDto) {
        MerchantEntity merchant = MerchantMapper.mapToEntity(merchantDto);
        return merchantRepository.save(merchant);
    }

    @Override
    public void updateTotalSum(Long merchantId) {
        MerchantEntity merchant = merchantRepository.findById(merchantId).get();
        merchant.setTotalTransactionSum(calculateTotalAmount(merchantId));
        merchantRepository.save(merchant);
    }

    private BigDecimal calculateTotalAmount(Long merchantId) {
        List<ApprovedTransactionEntity> transactionEntities
                = approvedTransactionRepository.findByMerchantId(merchantId);

        if (transactionEntities != null) {
            return transactionEntities
                    .stream()
                    .filter(transaction -> transaction.getStatus() != null && APPROVED_STATUS.contains(transaction.getStatus()))
                    .map(transaction -> makeRefundedTransactionNegative(transaction))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal makeRefundedTransactionNegative(ApprovedTransactionEntity transaction) {
        return transaction.getStatus().equals(TransactionStatus.REFUNDED)
                ? transaction.getAmount().negate()
                : transaction.getAmount();
    }
}
