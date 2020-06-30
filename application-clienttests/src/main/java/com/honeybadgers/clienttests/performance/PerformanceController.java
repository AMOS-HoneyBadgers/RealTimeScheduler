package com.honeybadgers.clienttests.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerformanceController {

    @Autowired
    PerformanceService performanceService;

    @GetMapping("/performance/{count}")
    public ResponseEntity<?> createPerformanceTest(@PathVariable(value = "count") final int count) {
        performanceService.createPostWithObject(count);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/performance/{listcount}/{taskcount}")
    public ResponseEntity<?> createPerformanceTest(@PathVariable(value = "listcount") final int listcount, @PathVariable(value = "taskcount") final int taskcount) {
        long before = System.currentTimeMillis();
        System.out.println("Before: " + before);
        performanceService.createBulkPostWithObject(listcount, taskcount);
        long after = System.currentTimeMillis();
        System.out.println("After: " + after);

        System.out.println("Diff: " + (after - before));
        return ResponseEntity.ok().build();
    }
}
