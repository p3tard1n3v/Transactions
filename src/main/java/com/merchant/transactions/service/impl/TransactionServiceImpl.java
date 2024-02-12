package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.ApprovedTransactionDto;
import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.mapper.ApprovedTransactionMapper;
import com.merchant.transactions.mapper.AuthorizeTransactionMapper;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.TransactionStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.AuthorizeTransactionRepository;
import com.merchant.transactions.repository.ErrorTransactionRepository;
import com.merchant.transactions.repository.ReversalTransactionRepository;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final AuthorizeTransactionRepository authorizeTransactionRepository;
    private final ApprovedTransactionRepository approvedTransactionRepository;
    private final ReversalTransactionRepository reversalTransactionRepository;
    private final ErrorTransactionRepository errorTransactionRepository;

    private final MerchantService merchantService;

    @Autowired
    public TransactionServiceImpl(final AuthorizeTransactionRepository authorizeTransactionRepository,
                                  final ApprovedTransactionRepository approvedTransactionRepository,
                                  final ErrorTransactionRepository errorTransactionRepository,
                                  final ReversalTransactionRepository reversalTransactionRepository,
                                  final MerchantService merchantService) {
        this.authorizeTransactionRepository = authorizeTransactionRepository;
        this.approvedTransactionRepository = approvedTransactionRepository;
        this.errorTransactionRepository = errorTransactionRepository;
        this.reversalTransactionRepository = reversalTransactionRepository;
        this.merchantService = merchantService;
    }
    @Override
    public AuthorizeTransactionEntity save(AuthorizeTransactionDto transactionDto) {
        AuthorizeTransactionEntity authorizeTransaction = AuthorizeTransactionMapper.mapToEntity(transactionDto);
        authorizeTransaction = save(authorizeTransaction);
        return authorizeTransaction;
    }

    @Override
    public void save(AuthorizeTransactionDto authorizeTransactionDto, MerchantEntity merchantEntity) {
        authorizeTransactionDto.setMerchant(merchantEntity);
        save(authorizeTransactionDto);
    }

    @Override
    public void deleteOldThanOneHourTransactions() {
        LocalDateTime nowMinusHour = LocalDateTime.now().minusHours(1);
        approvedTransactionRepository.deleteAllByLastUpdatedLessThan(nowMinusHour);
    }

    @Override
    public List<AuthorizeTransactionDto> findByMerchant(Long merchantId) {
        MerchantEntity merchant = merchantService.findById(merchantId);
        List<ApprovedTransactionDto> approvedTransactionDtos =
                ApprovedTransactionMapper.mapToDto(approvedTransactionRepository.findAllByMerchantId(merchantId));
        List<AuthorizeTransactionDto> authorizeTransactionDtos =
                AuthorizeTransactionMapper.mapToDto(authorizeTransactionRepository.findAllByMerchant(merchant));
        Map<UUID, AuthorizeTransactionDto> mapAuthorized = new HashMap<>();
        for (var authorized : authorizeTransactionDtos) {
            mapAuthorized.put(authorized.getId(), authorized);
        }

        for (var approved : approvedTransactionDtos) {
            UUID reference = approved.getReference();
            if (canBeReferenced(approved, reference)) {
                mapAuthorized.get(reference).setApprovedReferenceBy(approved);
            }
        }

        return authorizeTransactionDtos;
    }

    private static boolean canBeReferenced(ApprovedTransactionDto approved, UUID reference) {
        return reference != null && (
                approved.getStatus().equals(TransactionStatus.APPROVED)
                || approved.getStatus().equals(TransactionStatus.REFUNDED));
    }

    private AuthorizeTransactionEntity save(AuthorizeTransactionEntity transaction) {
        if (transaction.getReference() != null) {
            return errorTransactionRepository.save(AuthorizeTransactionMapper.mapToErrorTransaction(transaction));
        } else if (transaction.getAmount().equals(BigDecimal.ZERO)) {
            return reversalTransactionRepository.save(AuthorizeTransactionMapper.mapToReversalTransaction(transaction));
        }

        return authorizeTransactionRepository.save(transaction);
    }
}
