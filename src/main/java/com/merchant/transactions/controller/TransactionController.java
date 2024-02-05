package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.UserRole;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import com.merchant.transactions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TransactionController {
    private final MerchantService merchantService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final MerchantService merchantService, final UserService userService,
                                 final TransactionService transactionService) {
        this.merchantService = merchantService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/merchants/{merchantId}/transactions")
    public String showTransactions(@PathVariable("merchantId") long merchantId, Model model) {
        String username = SecurityUtil.getSessionUser();
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        UserEntity user = userService.findByUsername(username);
        if (user.getRole().equals(UserRole.USER) && !user.getId().equals(merchantDto.getUser().getId())){
            return "redirect:/merchants";
        }

        List<AuthorizeTransactionDto> transactionDto = transactionService.findByMerchant(merchantId);

        model.addAttribute("transactions", transactionDto);
        model.addAttribute("merchantName", merchantDto.getName());

        return "transactions-show";
    }
}
