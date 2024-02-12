package com.merchant.transactions;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.RefundTransactionEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.repository.ApprovedTransactionRepository;
import com.merchant.transactions.repository.MerchantRepository;
import com.merchant.transactions.repository.UserRepository;
import com.merchant.transactions.service.ChargeAndRefundService;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.RakeTaskService;
import com.merchant.transactions.service.TransactionService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionsApplicationTests {
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
	@Autowired
	private RakeTaskService rakeTaskService;
	@Autowired
	ChargeAndRefundService chargeAndRefundService;

	@Test
	void testMerchantServiceSaveAndDelete() {
		String name = "integrationTest1";
		merchantService.save(MerchantDto.builder()
				.name(name)
				.password("test")
				.status(MerchantStatus.ACTIVE)
				.totalTransactionSum(BigDecimal.ZERO)
				.email("test@test.com")
				.description("desc").build());

		assertThat(1).isEqualTo(merchantRepository.count());

		merchantService.delete(merchantService.findByName(name).getId());

		assertThat(0).isEqualTo(merchantRepository.count());
	}

	@Test
	public void testRakeImport() throws IOException {
		String stringToBeParsed = "" +
				"name,password,role,description,email,status,total_transaction_sum,username\n" +
				"Bagira,Bagira,user,Bagiradescription,bagira@gmail.com,active,0,test4\n" +
				"Lidl,Lidl,user,Lidldescription,lidl@gmail.com,active,0,test3\n" +
				"Bila,Bila,user,Biladescription,bila@gmail.com,inactive,0,test4\n" +
				"test5,test5,admin";
		StringReader reader = new StringReader(stringToBeParsed);
		CSVParser csvParser = CSVParser.parse(reader,
				CSVFormat.RFC4180.withFirstRecordAsHeader());

		rakeTaskService.importMerchants(csvParser);

		assertThat(3).isEqualTo(merchantRepository.count());
		assertThat(4).isEqualTo(userRepository.count());
		assertThat(MerchantStatus.INACTIVE).isEqualTo(merchantRepository.findByName("Bila").getStatus());
	}

	@Test
	void testApproveAndRefundOfTransactions() {
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
		assertThat(BigDecimal.ZERO).isEqualTo(merchantEntity.getTotalTransactionSum());

		AuthorizeTransactionDto transaction1 = AuthorizeTransactionDto.builder()
				.amount(new BigDecimal(100.23))
				.customerEmail("test1@transaction.com")
				.customerPhone("+359888345678")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity1 = transactionService.save(transaction1);

		BigDecimal amount2 = new BigDecimal(.23);
		AuthorizeTransactionDto transaction2 = AuthorizeTransactionDto.builder()
				.amount(amount2)
				.customerEmail("test2@transaction.com")
				.customerPhone("+359888111345")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity2 = transactionService.save(transaction2);

		BigDecimal amount3 = new BigDecimal(33333.23);
		AuthorizeTransactionDto transaction3 = AuthorizeTransactionDto.builder()
				.amount(amount3)
				.customerEmail("test3@transaction.com")
				.customerPhone("+359888111000")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity3 = transactionService.save(transaction3);

		AuthorizeTransactionDto transaction4 = AuthorizeTransactionDto.builder()
				.amount(BigDecimal.ZERO)
				.customerEmail("test4@transaction.com")
				.customerPhone("+359888111300")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity4 = transactionService.save(transaction4);

		AuthorizeTransactionDto transaction5 = AuthorizeTransactionDto.builder()
				.amount(new BigDecimal(300.3))
				.customerEmail("test5@transaction.com")
				.customerPhone("+359888111300")
				.reference(transEntity4.getId())
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity5 = transactionService.save(transaction5);

		ApprovedTransactionEntity approvedTransaction2 = chargeAndRefundService.approve(transEntity2);
		//merchantEntity = merchantService.findById(merchantEntity.getId());
		assertThat(1).isEqualTo(transEntity2.getAmount()
				.compareTo(merchantEntity.getTotalTransactionSum()));

		ApprovedTransactionEntity approvedTransaction = chargeAndRefundService.approve(transEntity3);
		BigDecimal sumApproved = sum(transEntity2.getAmount(), transEntity3.getAmount());
		assertThat(1).isEqualTo(sumApproved
				.compareTo(merchantEntity.getTotalTransactionSum()));

		RefundTransactionEntity refundTransaction = chargeAndRefundService.refund(approvedTransaction);
		assertThat(1).isEqualTo(transEntity2.getAmount()
				.compareTo(merchantEntity.getTotalTransactionSum()));
	}

	private BigDecimal sum(BigDecimal amount1, BigDecimal amount2) {
		return amount1.add(amount2);
	}

	@AfterEach
	public void deleteAfterEntities() {
		approvedTransactionRepository.deleteAll();
		merchantRepository.deleteAll();
		userRepository.deleteAll();
	}

	@BeforeEach
	public void deleteBeforeEntities() {
		approvedTransactionRepository.deleteAll();
		merchantRepository.deleteAll();
		userRepository.deleteAll();
	}
}
