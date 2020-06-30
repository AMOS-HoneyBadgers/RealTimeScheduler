package com.honeybadgers.clienttests.performance;

import com.honeybadgers.clienttests.models.ResponseModel;
import com.honeybadgers.clienttests.models.TaskModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PerformanceService {

    static final Logger logger = LogManager.getLogger(PerformanceService.class);

    private RestTemplate restTemplate;

    public void createPostWithObject(int count) {
        restTemplate = new RestTemplate();
        String url = "https://taskapi-amos.cfapps.io/api/task/";

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // list of all requests
        CompletableFuture<ResponseEntity<ResponseModel>>[] futures = new CompletableFuture[count];

        long before = System.currentTimeMillis();
        logger.info("Before: " + before);

        for(int i = 0; i < count; i++){
            futures[i] = sendRequest(headers, url, i);
        }

        CompletableFuture.allOf(futures).whenComplete((aVoid, throwable) -> {

            long after = System.currentTimeMillis();
            logger.info("After: " + after);

            if(throwable != null) {
                logger.error("At least one future failed!");
                logger.error(throwable.getMessage());
            } else {
                logger.info("Performance test finished with no errors!");
            }

            logger.info("Finished with timeDiff: " + (after - before));
        });
    }

    @Async("taskExecutorPerformance")
    public CompletableFuture<ResponseEntity<ResponseModel>> sendRequest(HttpHeaders headers, String url, int number) {
        // create a post object
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
        taskModel.setPriority(100);

        // build the request
        HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

        // send POST request
        ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
        logger.info("TaskNr: " + number);
        return CompletableFuture.completedFuture(response);
    }
}
