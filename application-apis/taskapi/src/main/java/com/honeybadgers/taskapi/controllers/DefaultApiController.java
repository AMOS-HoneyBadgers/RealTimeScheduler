package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.ErrorModel;
import com.honeybadgers.taskapi.models.ResponseModel;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:25.874+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Task Api.base-path:/api/task}")
public class DefaultApiController implements DefaultApi {

    @Autowired
    ITaskService taskService;


    static final Logger logger = LogManager.getLogger(DefaultApiController.class);

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public DefaultApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Creation of new Task
     * @param taskModel new task object (required)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> rootPost(@Valid TaskModel taskModel) {

        logger.info("Hi");

        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        if(taskModel == null){
            response.setCode("400");
            response.setMessage("Missing Body");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            taskService.createTask(taskModel);
            taskService.sendTaskToTaskEventQueue(taskModel.getId().toString());
        } catch (UnknownEnumException e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (JpaException | CreationException e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
