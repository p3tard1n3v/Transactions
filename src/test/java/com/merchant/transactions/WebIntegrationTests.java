package com.merchant.transactions;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.repository.UserRepository;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebIntegrationTests {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApprovedTransactionRepository approvedTransactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test
    void createTransactionForMerchantViaRestApi() {
        String merchantName = "integrationTest2";
        MerchantDto merchant = MerchantDto.builder()
                .name(merchantName)
                .password("merchant1")
                .email("test@merchant1.com")
                .description("test@merchant1.com merchant1")
                .status(MerchantStatus.ACTIVE)
                .totalTransactionSum(BigDecimal.ZERO)
                .build();
        MerchantEntity merchantEntity = merchantService.save(merchant);

        Map<String, String> bodyMap = new HashMap();
        bodyMap.put("amount", "11111111");
        bodyMap.put("customerEmail", "email23@emailadasdaad1.com");
        bodyMap.put("customerPhone", "0888345678");

        webClient
                .post().uri("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bodyMap))
                .headers(headers -> headers.setBasicAuth(merchantName, merchant.getPassword())).exchange().expectStatus().isCreated()
                .expectBody();
    }

    @Test
    void cannotTransactionForInactiveMerchantViaRestApi() {
        String merchantName = "integrationTest2";
        MerchantDto merchant = MerchantDto.builder()
                .name(merchantName)
                .password("merchant1")
                .email("test@merchant1.com")
                .description("test@merchant1.com merchant1")
                .status(MerchantStatus.INACTIVE)
                .totalTransactionSum(BigDecimal.ZERO)
                .build();
        MerchantEntity merchantEntity = merchantService.save(merchant);

        Map<String, String> bodyMap = new HashMap();
        bodyMap.put("amount", "11111111");
        bodyMap.put("customerEmail", "email23@emailadasdaad1.com");
        bodyMap.put("customerPhone", "0888345678");

        webClient
                .post().uri("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bodyMap))
                .headers(headers -> headers.setBasicAuth(merchantName, merchant.getPassword())).exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody();
    }

    @Test
    void deleteMerchantViaRestApi() {
        String merchantName = "integrationTest2";
        MerchantDto merchant = MerchantDto.builder()
                .name(merchantName)
                .password("merchant1")
                .email("test@merchant1.com")
                .description("test@merchant1.com merchant1")
                .status(MerchantStatus.INACTIVE)
                .totalTransactionSum(BigDecimal.ZERO)
                .build();
        MerchantEntity merchantEntity = merchantService.save(merchant);



        webClient
                .delete().uri("/merchants/"+merchantEntity.getId()+"/delete")
                .headers(headers -> headers.setBasicAuth(merchantName, merchant.getPassword())).exchange().expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody();
    }

    @Test
    void cannotDeleteMerchantIfHasTransactionsViaRestApi() {
        String merchantName = "integrationTest2";
        MerchantDto merchant = MerchantDto.builder()
                .name(merchantName)
                .password("merchant1")
                .email("test@merchant1.com")
                .description("test@merchant1.com merchant1")
                .status(MerchantStatus.INACTIVE)
                .totalTransactionSum(BigDecimal.ZERO)
                .build();
        MerchantEntity merchantEntity = merchantService.save(merchant);
        AuthorizeTransactionDto transaction1 = AuthorizeTransactionDto.builder()
                .amount(new BigDecimal(100.23))
                .customerEmail("test1@transaction.com")
                .customerPhone("+359888345678")
                .merchant(merchantEntity)
                .build();
        AuthorizeTransactionEntity transEntity1 = transactionService.save(transaction1);



        webClient
                .delete().uri("/merchants/"+merchantEntity.getId()+"/delete")
                .headers(headers -> headers.setBasicAuth(merchantName, merchant.getPassword())).exchange().expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody();
    }

    @Test
    void updateMerchantViaRestApi() {
        String merchantName = "integrationTest2";
        MerchantDto merchant = MerchantDto.builder()
                .name(merchantName)
                .password("merchant1")
                .email("test@merchant1.com")
                .description("test@merchant1.com merchant1")
                .status(MerchantStatus.INACTIVE)
                .totalTransactionSum(BigDecimal.ZERO)
                .build();
        MerchantEntity merchantEntity = merchantService.save(merchant);

        Map<String, String> bodyMap = new HashMap();
        bodyMap.put("id", merchantEntity.getId()+"");
        bodyMap.put("name", merchantEntity.getName());
        bodyMap.put("email", "email@email.com");
        bodyMap.put("description", "desc");
        bodyMap.put("status", "ACTIVE");
        bodyMap.put("totalTransactionSum", "0");

        webClient
                .patch().uri("/merchants/"+merchantEntity.getId()+"/edit")
                .headers(headers -> headers.setBasicAuth(merchantName, merchant.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bodyMap))
                .exchange().expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody();
    }

    @AfterEach
    public void deleteEntities() {
        approvedTransactionRepository.deleteAll();
        merchantRepository.deleteAll();
        userRepository.deleteAll();
    }
}
