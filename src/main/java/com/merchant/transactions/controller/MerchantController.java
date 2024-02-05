package com.merchant.transactions.controller;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.model.enums.UserRole;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        List<MerchantDto> merchantsDto = userService.isAdmin(username) ?
                merchantService.findAll() : merchantService.findByUserName(username);
        model.addAttribute("merchants", merchantsDto);

        return "merchants-list";
    }

    @GetMapping("/merchants/{merchantId}")
    public String showMerchant(@PathVariable("merchantId") long merchantId, Model model) {
        String username = SecurityUtil.getSessionUser();
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        UserEntity user = userService.findByUsername(username);
        if (user.getRole().equals(UserRole.USER) && !user.getId().equals(merchantDto.getUser().getId())){
            return "redirect:/merchants";
        }

        model.addAttribute("merchant", merchantDto);
        model.addAttribute("merchantCreated", formatDate(merchantDto.getCreated()));

        return "merchants-show";
    }

    @GetMapping("/merchants/{merchantId}/delete")
    public String deleteMerchant(@PathVariable("merchantId") Long merchantId) {
        if (merchantService.transactionsCountById(merchantId) == 0) {
            merchantService.delete(merchantId);
            return "redirect:/merchants";
        }

        return "redirect:/merchants/" + merchantId + "?error=true";
    }

    @GetMapping("/merchants/{merchantId}/edit")
    public String editMerchant(@PathVariable("merchantId") Long merchantId, Model model) {
        MerchantDto merchantDto = merchantService.findDtoById(merchantId);
        model.addAttribute("merchant", merchantDto);
        model.addAttribute("merchantStatuses", MerchantStatus.values());
        return "merchants-edit";
    }

    @PostMapping("/merchants/{merchantId}/edit")
    public String updateMerchant(@PathVariable("merchantId") Long merchantId,
                                 @Valid @ModelAttribute("merchant") MerchantDto merchant,
                                 BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("merchant", merchant);
            return "merchants-edit";
        }
        merchant.setId(merchantId);
        merchantService.updateMerchant(merchant);
        return "redirect:/merchants";
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
}
