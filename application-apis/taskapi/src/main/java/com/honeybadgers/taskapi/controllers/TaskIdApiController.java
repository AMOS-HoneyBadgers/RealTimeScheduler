package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:25.874+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Task Api.base-path:/api/task}")
public class TaskIdApiController implements TaskIdApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TaskIdApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<TaskModel> taskIdGet(Long taskId) {
        return ResponseEntity.ok(new TaskModel());
    }
}
