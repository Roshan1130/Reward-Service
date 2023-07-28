package org.rewardsprogram.sample.services;

import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.entity.Transaction;
import org.rewardsprogram.sample.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getLastThreeMonthTransactions(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        return transactionRepository.findAllByCustomerAndDateAfter(customer, threeMonthsAgo);
    }
}
