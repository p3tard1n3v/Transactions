package com.merchant.transactions.cronjob;

import com.merchant.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TransactionsScheduledTasksTests {
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    TransactionsScheduledTasks transactionsScheduledTasks;

    @Test
    public void testExecution() {
        transactionsScheduledTasks.execute();
        verify(transactionService).deleteOldThanOneHourTransactions();
        verifyNoMoreInteractions(transactionService);
    }

}
