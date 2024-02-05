package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
//@RequestMapping("/api/")
public class TransactionRestApiController {
    private final TransactionService transactionService;
    private final MerchantService merchantService;

    @Autowired
    public TransactionRestApiController(final TransactionService transactionService,
                                        final MerchantService merchantService) {
        this.transactionService = transactionService;
        this.merchantService = merchantService;
    }

    @PostMapping("/api/merchant/{merchantId}/transaction/create")
    public ResponseEntity<String> createTransaction(@PathVariable(value = "merchantId") long merchantId,
                                               @RequestBody AuthorizeTransactionDto authorizeTransactionDto) {

        MerchantEntity merchantEntity = merchantService.findById(merchantId);
        if (MerchantStatus.ACTIVE.equals(merchantEntity.getStatus())) {
            authorizeTransactionDto.setMerchant(merchantEntity);
            transactionService.save(authorizeTransactionDto);
            return new ResponseEntity<>("Transaction create", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Merchant is inactive. Transaction cannot be create", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/api/merchant")
    public ResponseEntity<String> getFoo() {

        return new ResponseEntity<>("Transaction create", HttpStatus.CREATED);
    }
}
