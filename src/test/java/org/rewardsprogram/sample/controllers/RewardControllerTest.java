package org.rewardsprogram.sample.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rewardsprogram.sample.entity.Customer;
import org.rewardsprogram.sample.entity.Transaction;
import org.rewardsprogram.sample.repositories.CustomerRepository;
import org.rewardsprogram.sample.repositories.TransactionRepository;
import org.rewardsprogram.sample.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RewardController.class)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private RewardService rewardService;

     @Test
    void getRewardPoints_shouldReturnRewardPointsForCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // May Transaction
        Transaction transaction1 = new Transaction();
        transaction1.setCustomer(customer);
        transaction1.setDate(LocalDate.now().minusMonths(2));
        transaction1.setAmount(150.0);

        // June Transaction
        Transaction transaction2 = new Transaction();
        transaction2.setCustomer(customer);
        transaction2.setDate(LocalDate.now().minusMonths(1));
        transaction2.setAmount(100.0);

        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAllByCustomerAndDateAfter(customer, LocalDate.now().minusMonths(3))).thenReturn(transactions);

        Map<String, Integer> expectedRewards = new HashMap<>();
        expectedRewards.put("June", 50);
        expectedRewards.put("May", 150);
        expectedRewards.put("TOTAL ", 200);

        when(rewardService.getRewardForCustomer(customer)).thenReturn(expectedRewards);

        mockMvc.perform(MockMvcRequestBuilders.get("/rewards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"June\":50,\"May\":150,\"TOTAL \":200}"));
    }

    @Test
    void getRewardPoints_shouldReturnOkWithZeroRewards_whenNoTransactions() throws Exception {
        // given
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findAllByCustomerAndDateAfter(customer, LocalDate.now().minusMonths(3)))
                .thenReturn(Collections.emptyList());

        Map<String, Integer> expectedRewards = new HashMap<>();
        expectedRewards.put("TOTAL: ", 0);

        when(rewardService.getRewardForCustomer(customer)).thenReturn(expectedRewards);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"TOTAL: \": 0}"));
    }

    @Test
    void getRewardPoints_shouldReturnNotFound_whenCustomerDoesNotExist() throws Exception {
        // given
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer not found"));
    }
}