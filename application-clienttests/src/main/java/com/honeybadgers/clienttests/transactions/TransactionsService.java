package com.honeybadgers.clienttests.transactions;

import com.honeybadgers.clienttests.models.ResponseModel;
import com.honeybadgers.clienttests.models.TaskModel;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.TaskStatusEnum;
import com.honeybadgers.postgre.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TransactionsService {
    RestTemplate restTemplate;

    @Autowired
    TaskRepository taskRepository;

    public void triggerScheduleWithTwoTransactions() {
        CompletableFuture<String> async1 = asyncSendRequests();
        CompletableFuture<String> async2 = asyncSendRequests();
        CompletableFuture.allOf(async1, async2).whenComplete((aVoid, throwable) -> {
            if (throwable != null)
                log.info(throwable.getMessage());
            else {
                try {
                    String taskid1 = async1.get();
                    String taskid2 = async2.get();
                    log.info("Taskid " + taskid1);
                    log.info("Taskid " + taskid2);
                    //check if one of the tasks was not scheduled
                    //Expect that one task status is waiting with no total priority and the other is dispatched
                    checkDatabaseForExpectedStatus(taskid1, taskid2);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                } catch (ExecutionException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }

    private void checkDatabaseForExpectedStatus(String taskid1, String taskid2) {
        Task task1 = taskRepository.findById(taskid1).orElse(null);
        Task task2 = taskRepository.findById(taskid2).orElse(null);
        if (task1.getStatus() == TaskStatusEnum.Dispatched)
            if (task2.getStatus() != TaskStatusEnum.Waiting)
                throw new RuntimeException("task2 " + taskid2 + " status should be Waiting but isn't. \n Check Transaction!");
        if (task2.getStatus() == TaskStatusEnum.Dispatched)
            if (task1.getStatus() != TaskStatusEnum.Waiting)
                throw new RuntimeException("task1 " + taskid1 + " status should be Waiting but isn't. \n Check Transaction!");
        log.info("DB check for task1 "+taskid1+" and task2 "+taskid2+" successfull");
    }

    @Async
    public CompletableFuture<String> asyncSendRequests() {
        restTemplate = new RestTemplate();
        String url = "https://taskapi-amos.cfapps.io/api/task/";

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // create a post object
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
        taskModel.setPriority(100);

        // build the request
        HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

        // send POST request
        ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
        return CompletableFuture.completedFuture(taskModel.getId().toString());


    }
}
