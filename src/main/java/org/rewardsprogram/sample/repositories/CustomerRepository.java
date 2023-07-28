package org.rewardsprogram.sample.repositories;

import org.rewardsprogram.sample.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

