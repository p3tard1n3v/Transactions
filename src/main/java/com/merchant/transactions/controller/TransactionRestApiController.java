package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionRestApiController {
    private final TransactionService transactionService;
    private final MerchantService merchantService;

    @Autowired
    public TransactionRestApiController(final TransactionService transactionService,
                                        final MerchantService merchantService) {
        this.transactionService = transactionService;
        this.merchantService = merchantService;
    }

    @PostMapping("/api/transactions/create")
    public ResponseEntity<String> createTransaction(@RequestBody AuthorizeTransactionDto authorizeTransactionDto) {
        MerchantEntity merchantEntity = merchantService.findEntityByCurrentUser();
        if (MerchantStatus.ACTIVE.equals(merchantEntity.getStatus())) {
            transactionService.save(authorizeTransactionDto, merchantEntity);
            return new ResponseEntity<>("Transaction create", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Merchant is inactive. Transaction cannot be create", HttpStatus.UNAUTHORIZED);
    }
}
