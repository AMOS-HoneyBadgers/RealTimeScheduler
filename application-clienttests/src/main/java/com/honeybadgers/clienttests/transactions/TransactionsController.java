package com.honeybadgers.clienttests.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TransactionsController {
    @Autowired
    TransactionsService transactionsService;


    @GetMapping("/triggerScheduleWithTwoTransactions")
    public ResponseEntity<?> createPerformanceTest() {
        transactionsService.triggerScheduleWithTwoTransactions();

        return ResponseEntity.ok().build();
    }
}





