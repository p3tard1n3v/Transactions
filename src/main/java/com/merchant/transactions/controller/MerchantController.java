package com.merchant.transactions.controller;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MerchantController {
    private final MerchantService merchantService;
    private final UserService userService;


    @Autowired
    public MerchantController(final MerchantService merchantService, final UserService userService) {
        this.merchantService = merchantService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/merchants";
    }


    @GetMapping("/merchants")
    public String listMerchants(Model model) {
        String username = SecurityUtil.getSessionUser();
        List<MerchantDto> merchantsDto = SecurityUtil.isAdminUser()?
                merchantService.findAll() : List.of(merchantService.findByName(username));
        model.addAttribute("merchants", merchantsDto);

        return "merchants-list";
    }

    @GetMapping("/merchants/{merchantId}")
    public String showMerchant(@PathVariable("merchantId") long merchantId, Model model) {
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        if (!merchantService.isAuthorized(merchantDto)){
            return "redirect:/merchants";
        }

        model.addAttribute("merchant", merchantDto);
        model.addAttribute("merchantCreated", formatDate(merchantDto.getCreated()));

        return "merchants-show";
    }

    @DeleteMapping("/merchants/{merchantId}/delete")
    public ResponseEntity<String> deleteMerchant(@PathVariable("merchantId") Long merchantId) {
        if (!merchantService.isAuthorized(merchantId)){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("");
        }

        if (merchantService.transactionsCountById(merchantId) == 0) {
            merchantService.delete(merchantId);
            return ResponseEntity.ok("Merchant deleted successfully!");
        }

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Merchant has active transactions, so cannot be deleted");
    }

    @GetMapping("/merchants/{merchantId}/edit")
    public String editMerchant(@PathVariable("merchantId") Long merchantId, Model model) {
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        if (!merchantService.isAuthorized(merchantDto)){
            return "redirect:/merchants";
        }
        model.addAttribute("merchant", merchantDto);
        model.addAttribute("merchantStatuses", MerchantStatus.values());
        return "merchants-edit";
    }

    @PatchMapping("/merchants/{merchantId}/edit")
    public ResponseEntity<String> updateMerchant(@PathVariable("merchantId") Long merchantId,
                                 @RequestBody MerchantDto merchant,
                                 BindingResult result) {
        if (!merchantService.isAuthorized(merchant.getId())){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("");
        }
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
        merchant.setId(merchantId);
        merchantService.updateMerchant(merchant);
        return ResponseEntity.ok("Merchant updated successfully!");
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
}
