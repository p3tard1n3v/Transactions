package com.merchant.transactions.controller;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.security.SecurityUtil;
import com.merchant.transactions.service.MerchantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MerchantControllerTests {

    @Mock
    private MerchantService merchantService;
    @InjectMocks
    private MerchantController merchantController;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor1;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor2;

    @Test
    public void tesHomePage() {
        assertThat("redirect:/merchants").isEqualTo(merchantController.homePage());
    }

    @Test
    public void testShowMerchantIfIsAuthorized() {
        long merchantId = 1L;
        Model model = mock(Model.class);
        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantDto.getCreated()).thenReturn(LocalDateTime.now());
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(true);

        String result = merchantController.showMerchant(merchantId, model);

        assertThat("merchants-show").isEqualTo(result);
        verify(model).addAttribute("merchant", merchantDto);
        verify(model).addAttribute(stringArgumentCaptor1.capture(), stringArgumentCaptor2.capture());
        assertThat("merchantCreated").isEqualTo(stringArgumentCaptor1.getValue());
        assertThat(formatDate(merchantDto.getCreated())).isEqualTo(stringArgumentCaptor2.getValue());
    }

    @Test
    public void testShowMerchantIfIsNotAuthorized() {
        long merchantId = 1L;
        Model model = mock(Model.class);
        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(false);

        String result = merchantController.showMerchant(merchantId, model);

        assertThat("redirect:/merchants").isEqualTo(result);
    }

    @Test
    public void testListMerchantsIfUserIsAdmin() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(true);
            List<MerchantDto> merchantsDto = mock(List.class);
            when(merchantService.findAll()).thenReturn(merchantsDto);
            Model model = mock(Model.class);

            String result = merchantController.listMerchants(model);

            assertThat("merchants-list").isEqualTo(result);
            verify(model).addAttribute("merchants", merchantsDto);
        }
    }

    @Test
    public void testListMerchantsIfUserIsNotAdmin() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            String username = "adminUser";

            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn(username);
            mockedStatic.when(SecurityUtil::isAdminUser).thenReturn(false);
            MerchantDto merchantDto = mock(MerchantDto.class);
            when(merchantService.findByName(username)).thenReturn(merchantDto);
            Model model = mock(Model.class);

            String result = merchantController.listMerchants(model);

            assertThat("merchants-list").isEqualTo(result);
            verify(model).addAttribute(eq("merchants"), eq(List.of(merchantDto)));
        }
    }

    @Test
    public void testEditMerchantsIfUserIsNotAuthorized() {
        long merchantId = 5L;
        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(false);

        String result = merchantController.editMerchant(merchantId, mock(Model.class));

        assertThat("redirect:/merchants").isEqualTo(result);
    }

    @Test
    public void testEditMerchantsIfUserIsAuthorized() {
        long merchantId = 5L;
        MerchantDto merchantDto = mock(MerchantDto.class);
        when(merchantService.findDtoById(merchantId)).thenReturn(merchantDto);
        when(merchantService.isAuthorized(merchantDto)).thenReturn(true);

        Model model = mock(Model.class);
        String result = merchantController.editMerchant(merchantId, model);

        assertThat("merchants-edit").isEqualTo(result);

        verify(model).addAttribute("merchant", merchantDto);
        verify(model).addAttribute("merchantStatuses", MerchantStatus.values());
    }

    @Test
    public void testDeleteMerchantIsNotAuthorized() {
        Long merchantId = 67L;
        when(merchantService.isAuthorized(merchantId)).thenReturn(false);

        ResponseEntity<String> result = merchantController.deleteMerchant(merchantId);
        assertThat(HttpStatus.METHOD_NOT_ALLOWED.value()).isEqualTo(result.getStatusCode().value());
        assertThat("").isEqualTo(result.getBody());
    }

    @Test
    public void testDeleteMerchantIsAuthorizedAndHasNoTransactions() {
        Long merchantId = 67L;
        when(merchantService.isAuthorized(merchantId)).thenReturn(true);
        when(merchantService.transactionsCountById(merchantId)).thenReturn(0);

        ResponseEntity<String> result = merchantController.deleteMerchant(merchantId);
        assertThat(HttpStatus.OK.value()).isEqualTo(result.getStatusCode().value());
        assertThat("Merchant deleted successfully!").isEqualTo(result.getBody());
        verify(merchantService).delete(merchantId);
    }

    @Test
    public void testDeleteMerchantIsAuthorizedButHasTransactions() {
        Long merchantId = 67L;
        when(merchantService.isAuthorized(merchantId)).thenReturn(true);
        when(merchantService.transactionsCountById(merchantId)).thenReturn(300);

        ResponseEntity<String> result = merchantController.deleteMerchant(merchantId);
        assertThat(HttpStatus.NOT_ACCEPTABLE.value()).isEqualTo(result.getStatusCode().value());
        assertThat("Merchant has active transactions, so cannot be deleted").isEqualTo(result.getBody());
    }

    @Test
    public void testUpdateMerchantIsNotAuthorized() {
        Long merchantId = 67L;
        MerchantDto merchant = mock(MerchantDto.class);
        when(merchant.getId()).thenReturn(67L);
        BindingResult bindingResult = mock(BindingResult.class);
        when(merchantService.isAuthorized(merchant.getId())).thenReturn(false);

        ResponseEntity<String> result = merchantController.updateMerchant(merchantId, merchant, bindingResult);

        assertThat(HttpStatus.METHOD_NOT_ALLOWED.value()).isEqualTo(result.getStatusCode().value());
        assertThat("").isEqualTo(result.getBody());
    }

    @Test
    public void testUpdateMerchantIsAuthorizedButHasErrors() {
        Long merchantId = 67L;
        MerchantDto merchant = mock(MerchantDto.class);
        when(merchant.getId()).thenReturn(67L);
        BindingResult bindingResult = mock(BindingResult.class);
        when(merchantService.isAuthorized(merchant.getId())).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<String> result = merchantController.updateMerchant(merchantId, merchant, bindingResult);

        assertThat(HttpStatus.BAD_REQUEST.value()).isEqualTo(result.getStatusCode().value());
        assertThat("").isEqualTo(result.getBody());
    }

    @Test
    public void testUpdateMerchantSuccessfully() {
        Long merchantId = 67L;
        MerchantDto merchant = mock(MerchantDto.class);
        when(merchant.getId()).thenReturn(67L);
        BindingResult bindingResult = mock(BindingResult.class);
        when(merchantService.isAuthorized(merchant.getId())).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<String> result = merchantController.updateMerchant(merchantId, merchant, bindingResult);

        assertThat(HttpStatus.OK.value()).isEqualTo(result.getStatusCode().value());
        assertThat("Merchant updated successfully!").isEqualTo(result.getBody());
        verify(merchantService).updateMerchant(merchant);
    }


    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
}
