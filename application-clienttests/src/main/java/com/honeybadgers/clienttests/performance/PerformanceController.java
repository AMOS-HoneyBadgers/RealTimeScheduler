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

    @GetMapping("/performance")
    public ResponseEntity<?> createPerformanceTest() {
        performanceService.createPostWithObject();
        return ResponseEntity.ok().build();
    }
}
