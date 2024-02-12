package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TransactionController {
    private final MerchantService merchantService;
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final MerchantService merchantService,
                                 final TransactionService transactionService) {
        this.merchantService = merchantService;
        this.transactionService = transactionService;
    }

    @GetMapping("/merchants/{merchantId}/transactions")
    public String showTransactions(@PathVariable("merchantId") long merchantId, Model model) {
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        if (!merchantService.isAuthorized(merchantDto)){
            return "redirect:/merchants";
        }

        List<AuthorizeTransactionDto> transactionDto = transactionService.findByMerchant(merchantId);

        model.addAttribute("transactions", transactionDto);
        model.addAttribute("merchantName", merchantDto.getName());

        return "transactions-show";
    }
}
