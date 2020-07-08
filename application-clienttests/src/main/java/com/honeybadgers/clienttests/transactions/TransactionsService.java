package com.honeybadgers.clienttests.transactions;

import com.honeybadgers.clienttests.models.ResponseModel;
import com.honeybadgers.clienttests.models.TaskModel;
import com.honeybadgers.communication.ICommunication;
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

    @Autowired
    ICommunication communication;

    /**
     * Create two tasks asynchronously. Check if the expected status of the tasks is present in the database
     * (Dispatched and Waiting)
     */
    public void triggerScheduleWithTwoTransactions() {
        CompletableFuture<String> async1 = asyncCreateTaskRequest();
        CompletableFuture<String> async2 = asyncCreateTaskRequest();
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

    /**
     * Checks the database for two tasks. Successfull if one is dispatched and the other is waiting
     * @param taskid1 id of task1
     * @param taskid2 id of task2
     */
    private void checkDatabaseForExpectedStatus(String taskid1, String taskid2) {
        Task task1 = taskRepository.findById(taskid1).orElse(null);
        Task task2 = taskRepository.findById(taskid2).orElse(null);
        if (task1.getStatus() == TaskStatusEnum.Dispatched)
            if (task2.getStatus() != TaskStatusEnum.Waiting)
                throw new RuntimeException("task2 " + taskid2 + " status should be Waiting but isn't. \n Check Transaction!");
        if (task2.getStatus() == TaskStatusEnum.Dispatched)
            if (task1.getStatus() != TaskStatusEnum.Waiting)
                throw new RuntimeException("task1 " + taskid1 + " status should be Waiting but isn't. \n Check Transaction!");
        log.info("DB check for task1 " + taskid1 + " and task2 " + taskid2 + " successfull");
    }

    /**
     * Creates a asynchronous HTTP api call for task creations
     * @return CompletableFuture Object of task
     */
    @Async
    public CompletableFuture<String> asyncCreateTaskRequest() {
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
        taskModel.setId(UUID.randomUUID().toString());
        taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
        taskModel.setPriority(100);

        // build the request
        HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

        // send POST request
        ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
        return CompletableFuture.completedFuture(taskModel.getId().toString());
    }

    /**
     * Creates a async call for feedback and update of the same taskid both done in two transactions
     * Check the database for status finished afterwards
     * @param taskid taskid which is processed
     */
    public void triggerSendFeedbackAndUpdateTaskWithTwoTransactions(String taskid) {
        CompletableFuture<String> async1 = asyncSendFeedback(taskid);
        CompletableFuture<String> async2 = asyncUpdateTaskRequest(taskid);
        CompletableFuture.allOf(async1, async2).whenComplete((aVoid, throwable) -> {
            if (throwable != null)
                log.info(throwable.getMessage());
            else {
                try {
                    checkDatabaseForFinishedStatus(taskid);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }

    /**
     * Check several times if the corresponding task has the status finished after a defined time period.
     * We expect the task to be updated at least after 4 time periods.
     * The reason for the check is the conflicting update + sendFeedbackToScheduler Operation.
     *
     * @param taskid for task to be checked
     */
    private void checkDatabaseForFinishedStatus(String taskid) throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            Task expected = taskRepository.findById(taskid).orElse(null);
            if (expected == null)
                throw new RuntimeException("Task " + taskid + " not found");
            if (expected.getStatus() == TaskStatusEnum.Finished) {
                log.info("task " + taskid + " status was set successfully to finished ");
                return;
            }
            Thread.sleep(1000);
        }
        throw new RuntimeException("Task " + taskid + " didn't reach the status finished after about 4 seconds as expected");
    }

    /**
     * send asynchronous feedback to scheduler
     * @param taskid task id which is finished
     * @return CompletableFuture Object
     */
    @Async
    public CompletableFuture<String> asyncSendFeedback(String taskid) {
        communication.sendFeedbackToScheduler(taskid);
        return CompletableFuture.completedFuture(taskid);
    }

    /**
     * Creates a asynchronous HTTP api call for task updates
     * @param taskid the task id to be updated
     * @return CompletableFuture Object of task
     */
    @Async
    public CompletableFuture<String> asyncUpdateTaskRequest(String taskid) {
        restTemplate = new RestTemplate();

        // create a post object
        TaskModel taskModel = new TaskModel();
        taskModel.setId(taskid);
        taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
        taskModel.setPriority(100);

        String url = "https://taskapi-amos.cfapps.io/api/task/" + taskModel.getId().toString();

        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

        // send POST request
        ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
        return CompletableFuture.completedFuture(taskid);
    }
}
