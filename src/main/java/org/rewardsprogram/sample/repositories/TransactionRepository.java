package org.rewardsprogram.sample.repositories;

import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCustomerAndDateAfter(Customer customer, LocalDate threeMonthsAgo);
}

