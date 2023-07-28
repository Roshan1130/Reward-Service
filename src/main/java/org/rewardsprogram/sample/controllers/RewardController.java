package org.rewardsprogram.sample.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.repositories.CustomerRepository;
import org.rewardsprogram.sample.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RewardService rewardService;


    @GetMapping("/{customerId}")
    public String getRewardPoints(@PathVariable Long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            return "Customer not found";
        }
        Customer customer = optionalCustomer.get();
        Map<String, Integer> rewards = rewardService.getRewardForCustomer(customer);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(rewards);
        } catch (JsonProcessingException e) {
            return "Error processing rewards";
        }
    }
}

