package com.merchant.transactions.cronjob;

import com.merchant.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionsScheduledTasks {
    private final TransactionService transactionService;

    @Autowired
    public TransactionsScheduledTasks(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 0/30 * * * *") // Cron expression for running every half of hour
    public void execute() {
        System.out.println("TransactionsScheduledTasks");
        transactionService.deleteOldThanOneHourTransactions();
    }

}
