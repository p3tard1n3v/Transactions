package com.merchant.transactions;

import com.merchant.transactions.dto.AuthorizeTransactionDto;
import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.ApprovedTransactionEntity;
import com.merchant.transactions.model.AuthorizeTransactionEntity;
import com.merchant.transactions.model.MerchantEntity;
import com.merchant.transactions.model.UserEntity;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.model.enums.UserRole;
import com.merchant.transactions.service.*;
import com.merchant.transactions.service.impl.NotAllowedOperationRefundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;

@SpringBootApplication
public class TransactionsApplication implements CommandLineRunner {
	public static final String RAKE_TASK_CVS_IMPORT = "rake:task:cvs:import";
	public static final String IMPORT_MERCHANTS = ":merchants=";
	public static final String IMPORT_USERS = ":users=";
	private final UserService userService;
	private final MerchantService merchantService;
	private final AuthorizeTransactionService authorizeTransactionService;
	private final ChargeAndRefundService chargeAndRefundService;
	private final RakeTaskService rakeTaskService;

	@Autowired
	public TransactionsApplication(final UserService userService,
								   final MerchantService merchantService,
								   final AuthorizeTransactionService authorizeTransactionService,
								   final ChargeAndRefundService chargeAndRefundService,
								   final RakeTaskService rakeTaskService) {
		this.userService = userService;
		this.merchantService = merchantService;
		this.authorizeTransactionService = authorizeTransactionService;
		this.chargeAndRefundService = chargeAndRefundService;
		this.rakeTaskService = rakeTaskService;
	}
	public static void main(String[] args) {
		SpringApplication.run(TransactionsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!(args.length > 0 && args[0].startsWith(RAKE_TASK_CVS_IMPORT))) return;
		rakeTask(args);
	}

	private void rakeTask(String... args) {
		System.out.println(args[0]);
		String[] arguments = args[0].split(",");
		for (var arg : arguments) {
			if (arg.startsWith(RAKE_TASK_CVS_IMPORT + IMPORT_MERCHANTS)) {
				rakeTaskService.importMerchants(parseFileName(arg));
			} else if (arg.startsWith(RAKE_TASK_CVS_IMPORT + IMPORT_USERS)) {
				rakeTaskService.importUsers(parseFileName(arg));
			}
		}

		System.exit(0);
	}

	private String parseFileName(String arg) {
		String[] nameValue = arg.split("=");
		return nameValue[1];
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		UserEntity userEntity = populateWithUsers();
		populateWitMerchantAndTransactions(userEntity);
	}

	private UserEntity populateWithUsers() {
		UserEntity userEntityWithUserRole = null;
		if (userService.usersCount() == 0) {
			userService.save(UserDto.builder()
					.username("test1")
					.password("test1")
					.role(UserRole.ADMIN)
					.build());

			UserEntity user2 = userService.save(UserDto.builder()
					.username("test2")
					.password("test2")
					.role(UserRole.USER)
					.build());

			userEntityWithUserRole = user2;
		}

		return userEntityWithUserRole;
	}

	private void populateWitMerchantAndTransactions(UserEntity userEntity) {
		if (userEntity == null) {
			return;
		}

		MerchantDto merchant = MerchantDto.builder()
				.name("merchant1")
				.email("test@merchant1.com")
				.description("test@merchant1.com merchant1")
				.status(MerchantStatus.ACTIVE)
				.totalTransactionSum(BigDecimal.ZERO)
				.user(userEntity)
				.build();
		MerchantEntity merchantEntity = merchantService.save(merchant);

		AuthorizeTransactionDto transaction1 = AuthorizeTransactionDto.builder()
				.amount(new BigDecimal(100.23))
				.customerEmail("test1@transaction.com")
				.customerPhone("+359888345678")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity1 = authorizeTransactionService.save(transaction1);

		BigDecimal amount2 = new BigDecimal(.23);
		AuthorizeTransactionDto transaction2 = AuthorizeTransactionDto.builder()
				.amount(amount2)
				.customerEmail("test2@transaction.com")
				.customerPhone("+359888111345")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity2 = authorizeTransactionService.save(transaction2);

		BigDecimal amount3 = new BigDecimal(33333.23);
		AuthorizeTransactionDto transaction3 = AuthorizeTransactionDto.builder()
				.amount(amount3)
				.customerEmail("test3@transaction.com")
				.customerPhone("+359888111000")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity3 = authorizeTransactionService.save(transaction3);

		AuthorizeTransactionDto transaction4 = AuthorizeTransactionDto.builder()
				.amount(BigDecimal.ZERO)
				.customerEmail("test4@transaction.com")
				.customerPhone("+359888111300")
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity4 = authorizeTransactionService.save(transaction4);

		AuthorizeTransactionDto transaction5 = AuthorizeTransactionDto.builder()
				.amount(new BigDecimal(300.3))
				.customerEmail("test5@transaction.com")
				.customerPhone("+359888111300")
				.reference(transEntity4.getId())
				.merchant(merchantEntity)
				.build();
		AuthorizeTransactionEntity transEntity5 = authorizeTransactionService.save(transaction5);

		chargeAndRefundService.approve(transEntity2);
		ApprovedTransactionEntity approvedTransaction = chargeAndRefundService.approve(transEntity3);
		try {
			chargeAndRefundService.refund(approvedTransaction);
		} catch (NotAllowedOperationRefundException e) {
			System.out.println(e.getMessage());
		}
	}

	private BigDecimal totalSum(Long merchantId) {
		MerchantEntity merchant = merchantService.findById(merchantId);
		return merchant.getTotalTransactionSum() != null ? merchant.getTotalTransactionSum() : BigDecimal.ZERO;
	}
}
