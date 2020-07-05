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

    /**
     * send an amount of tasks create calls as Future Objects to the api
     * @param count how many tasks should be created
     */
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

    /**
     * Async REST call to the specified url
     * @param headers standard http headers
     * @param url url to connect
     * @param number handed over to the method in order to print out current task
     * @return CompletableFuture Object of response
     */
    @Async("taskExecutorPerformance")
    public CompletableFuture<ResponseEntity<ResponseModel>> sendRequest(HttpHeaders headers, String url, int number) {
        // create a post object
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID().toString());
        taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
        taskModel.setPriority(100);

        // build the request
        HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

        // send POST request
        ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
        logger.info("TaskNr: " + number);
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Connect to bulk endpoint of task api and send multiple tasks within separate lists
     * Example: listCount: 20, taskCount: 50 --> 20 lists with each 50 tasks = 1000 tasks
     * @param listCount amount of lists for tasks
     * @param taskCount amount of tasks per list
     */
    public void createBulkPostWithObject(int listCount, int taskCount) {
        restTemplate = new RestTemplate();
        String url = "https://taskapi-amos.cfapps.io/api/task/tasks";

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a post object

        for(int i = 0; i < listCount; i++){
            List<TaskModel> taskList = new ArrayList<TaskModel>();
            for(int j = 0; j < taskCount; j++){
                TaskModel taskModel = new TaskModel();
                taskModel.setId(UUID.randomUUID().toString());
                taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
                taskModel.setPriority(100);
                taskList.add(taskModel);
            }

            // build the request
            HttpEntity<List<TaskModel>> entity = new HttpEntity<>(taskList, headers);

            // send POST request
            ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
            System.out.println("TaskList Nr: " + i + ", with " + taskCount + " tasks per List");
        }
    }
}
