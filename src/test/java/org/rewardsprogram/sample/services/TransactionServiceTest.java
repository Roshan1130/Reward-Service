package org.rewardsprogram.sample.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.entity.Transaction;
import org.rewardsprogram.sample.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TransactionRepository.class, TransactionService.class})
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    void getLastThreeMonthTransactions_shouldReturnTransactionsForCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);

        // Add transaction on last 2 month
        Transaction transaction1 = new Transaction();
        transaction1.setCustomer(customer);
        transaction1.setDate(LocalDate.now().minusMonths(2));

        // Add transaction on last 1 month
        Transaction transaction2 = new Transaction();
        transaction2.setCustomer(customer);
        transaction2.setDate(LocalDate.now().minusMonths(1));

        List<Transaction> expectedTransactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAllByCustomerAndDateAfter(customer, LocalDate.now().minusMonths(3))).thenReturn(expectedTransactions);

        List<Transaction> actualTransactions = transactionService.getLastThreeMonthTransactions(customer);

        assertEquals(expectedTransactions, actualTransactions);
    }

    @Test
    public void getLastThreeMonthTransactions_shouldReturnEmptyList_whenCustomerHasNoTransactionsInThePastThreeMonths() {
        Customer customer = new Customer();
        List<Transaction> transactions = transactionService.getLastThreeMonthTransactions(customer);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void getLastThreeMonthTransactions_shouldReturnListOfTransactions_whenCustomerHasTransactionsInThePastThreeMonths() {
        Customer customer = new Customer();
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setDate(LocalDate.now().minusMonths(3));

        when(transactionRepository.findAllByCustomerAndDateAfter(customer, LocalDate.now().minusMonths(3))).thenReturn(Arrays.asList(transaction));

        List<Transaction> transactions = transactionService.getLastThreeMonthTransactions(customer);

        assertThat(transactions, is(not(empty())));
        assertEquals(1, transactions.size());
        assertThat(transactions.get(0), is(transaction));
    }

    @Test
    public void getLastThreeMonthTransactions_shouldThrowException_whenCustomerIsNotFound() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.getLastThreeMonthTransactions(null));
    }


}
