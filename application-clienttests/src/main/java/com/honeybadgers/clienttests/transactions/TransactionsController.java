package com.honeybadgers.clienttests.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TransactionsController {
    @Autowired
    TransactionsService transactionsService;


    @GetMapping("/triggerScheduleWithTwoTransactions")
    public ResponseEntity<?> triggerScheduleWithTwoTransactions() {
        transactionsService.triggerScheduleWithTwoTransactions();
        return ResponseEntity.ok().build();
    }
    @GetMapping("/triggerSendFeedbackAndUpdateTaskWithTwoTransactions/{taskid}")
    public ResponseEntity<?> triggerSendFeedbackAndUpdateTaskWithTwoTransactions(@PathVariable String taskid) {
        transactionsService.triggerSendFeedbackAndUpdateTaskWithTwoTransactions(taskid);
        return ResponseEntity.ok().build();
    }

}





