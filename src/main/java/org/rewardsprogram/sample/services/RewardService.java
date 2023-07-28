package org.rewardsprogram.sample.services;

import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardService {
    @Autowired
    TransactionService transactionService;

    public Map<String, Integer> getRewardForCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        Map<String, Integer> rewards = new HashMap<>();
        int totalPoints = 0;

        List<Transaction> transactions = transactionService.getLastThreeMonthTransactions(customer);


        for (Transaction transaction : transactions) {
            String month = transaction.getDate().getMonth().name();
            int points = calculateRewardPoints(transaction.getAmount());

            rewards.put(month, rewards.getOrDefault(month, 0) + points);
            totalPoints += points;
        }
        rewards.put("TOTAL: ", totalPoints);
        return rewards;
    }

    public int calculateRewardPoints(Double amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (amount > 100) {
            return (int) (2 * (amount - 100) + 50);
        } else if (amount >= 50) {
            return (int) (amount - 50);
        } else {
            return 0;
        }
    }
}

