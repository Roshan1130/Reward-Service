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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RewardService.class, TransactionService.class})
public class RewardServiceTest {
    @Autowired
    private RewardService rewardService;
    @Autowired
    private TransactionService transactionService;
    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    public void calculateRewardPoints_shouldReturnZero_whenAmountIsLessThan50() {
        int points = rewardService.calculateRewardPoints(49.0);

        assertThat(points, is(0));
    }

    @Test
    public void calculateRewardPoints_shouldReturnCorrectPoints_whenAmountIs50() {
        int points = rewardService.calculateRewardPoints(50.0);

        assertThat(points, is(0));
    }

    @Test
    public void calculateRewardPoints_shouldReturnCorrectPoints_whenAmountIsBetween50And100() {
        int points = rewardService.calculateRewardPoints(75.0);

        assertThat(points, is(25));
    }

    @Test
    public void calculateRewardPoints_shouldReturnCorrectPoints_whenAmountIsGreaterThan100() {
        int points = rewardService.calculateRewardPoints(120.0);

        assertThat(points, is(90));
    }

    @Test
    public void calculateRewardPoints_shouldReturnCorrectPoints_whenAmountIs100() {
        int points = rewardService.calculateRewardPoints(100.0);

        assertThat(points, is(50));
    }

    @Test
    public void calculateRewardPoints_shouldThrowException_whenAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> rewardService.getRewardForCustomer(null));
    }

    @Test
    void getRewardForCustomer_shouldReturnRewardsForCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);

        // May Transaction
        Transaction transaction1 = new Transaction();
        transaction1.setCustomer(customer);
        transaction1.setDate(LocalDate.now().minusMonths(2));
        transaction1.setAmount(200.0);

        // June Transaction
        Transaction transaction2 = new Transaction();
        transaction2.setCustomer(customer);
        transaction2.setDate(LocalDate.now().minusMonths(1));
        transaction2.setAmount(100.0);

        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAllByCustomerAndDateAfter(customer, LocalDate.now().minusMonths(3))).thenReturn(transactions);

        Map<String, Integer> expectedRewards = new HashMap<>();
        expectedRewards.put("MAY", 250);
        expectedRewards.put("JUNE", 50);
        expectedRewards.put("TOTAL: ", 300);

        Map<String, Integer> actualRewards = rewardService.getRewardForCustomer(customer);

        assertEquals(expectedRewards, actualRewards);
    }

    @Test
    public void getRewardForCustomer_shouldReturnMapWith_totalZero_whenNoTransactions() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(transactionService.getLastThreeMonthTransactions(customer)).thenReturn(Collections.emptyList());

        Map<String, Integer> rewards = rewardService.getRewardForCustomer(customer);

        assertFalse(rewards.isEmpty(), "Rewards is not empty");
        assertEquals(1, rewards.size(), "Rewards map should contain only one entry");
        assertTrue(rewards.containsKey("TOTAL: "), "Rewards map should contain 'TOTAL: ' key");
        assertEquals(0, rewards.get("TOTAL: ").intValue(), "Total reward points should be zero");
    }

    @Test
    public void getRewardForCustomer_shouldThrowException_whenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> rewardService.getRewardForCustomer(null));
    }


}

