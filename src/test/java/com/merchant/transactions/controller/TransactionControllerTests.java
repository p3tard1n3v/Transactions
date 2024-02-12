package com.merchant.transactions.controller;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionControllerTests {

    @Mock
    private MerchantService merchantService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void testShowTransactionsIsNotAuthorized() {
        long merchantId = 56L;
        Model model = mock(Model.class);

        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(false);

        String result = transactionController.showTransactions(merchantId, model);

        assertThat("redirect:/merchants").isEqualTo(result);
    }

    @Test
    public void testShowTransactionsSuccessfullyWhenIsAuthorized() {
        long merchantId = 56L;
        Model model = mock(Model.class);

        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantDto.getName()).thenReturn("bila");
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(true);

        List<AuthorizeTransactionDto> transactionDto = mock(List.class);
        when(transactionService.findByMerchant(merchantId)).thenReturn(transactionDto);

        String result = transactionController.showTransactions(merchantId, model);

        assertThat("transactions-show").isEqualTo(result);

        verify(model).addAttribute("transactions", transactionDto);
        verify(model).addAttribute("merchantName", merchantDto.getName());
    }
}
