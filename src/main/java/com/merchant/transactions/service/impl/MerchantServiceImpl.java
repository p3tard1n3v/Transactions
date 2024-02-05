package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.mapper.MerchantMapper;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.repository.UserRepository;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.MerchantService;
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
    private final MerchantRepository merchantRepository;
    private final ApprovedTransactionRepository approvedTransactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public MerchantServiceImpl(final MerchantRepository merchantRepository,
                               final ApprovedTransactionRepository approvedTransactionRepository,
                               final UserRepository userRepository) {
        this.merchantRepository = merchantRepository;
        this.approvedTransactionRepository = approvedTransactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MerchantEntity findById(Long merchantId) {
        return merchantRepository.findById(merchantId).get();
    }

    @Override
    public MerchantDto findDtoById(Long merchantId) {
        return MerchantMapper.mapToDto(findById(merchantId));
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

    @Override
    public List<MerchantDto> findByUserName(String username) {
        return MerchantMapper.mapToDto(merchantRepository.findByUserName(username));
    }

    @Override
    public List<MerchantDto> findAll() {
        return MerchantMapper.mapToDto(merchantRepository.findAll());
    }

    @Override
    public int transactionsCountById(Long merchantId) {
        return merchantRepository.transactionsCountById(merchantId);
    }

    @Override
    public void updateMerchant(MerchantDto merchantDto) {
        MerchantEntity merchant = merchantRepository.findById(merchantDto.getId()).get();
        merchant.setStatus(merchantDto.getStatus());
        merchant.setName(merchantDto.getName());
        merchant.setEmail(merchantDto.getEmail());
        merchant.setDescription(merchantDto.getDescription());
        merchant.setTotalTransactionSum(merchantDto.getTotalTransactionSum());

        String username = SecurityUtil.getSessionUser();
        UserEntity user = userRepository.findByUsername(username);
        merchant.setUser(user);

        merchantRepository.save(merchant);
    }

    @Override
    public void delete(Long merchantId) {
        merchantRepository.deleteById(merchantId);
    }

    private BigDecimal calculateTotalAmount(Long merchantId) {
        List<ApprovedTransactionEntity> transactionEntities
                = approvedTransactionRepository.findAllByMerchantId(merchantId);

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
