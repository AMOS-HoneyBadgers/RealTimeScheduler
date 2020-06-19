package com.honeybadgers.clienttests.performance;

import com.honeybadgers.clienttests.models.ResponseModel;
import com.honeybadgers.clienttests.models.TaskModel;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PerformanceService {

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

        // create a post object

        for(int i = 0; i < count; i++){
            TaskModel taskModel = new TaskModel();
            taskModel.setId(UUID.randomUUID());
            taskModel.setGroupId("TestGroupRunAlwaysNoLimit");
            taskModel.setPriority(100);

            // build the request
            HttpEntity<TaskModel> entity = new HttpEntity<>(taskModel, headers);

            // send POST request
            ResponseEntity<ResponseModel> response = restTemplate.postForEntity(url, entity, ResponseModel.class);
            System.out.println("TaskNr: " + i);
        }
    }
}
