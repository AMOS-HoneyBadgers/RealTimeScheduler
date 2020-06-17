package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.models.ResponseModel;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:25.874+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Task Api.base-path:/api/task}")
public class TaskIdApiController implements TaskIdApi {

    final static Logger logger = LogManager.getLogger(TaskIdApiController.class);

    private final NativeWebRequest request;

    @Autowired
    ITaskService taskservice;

    @org.springframework.beans.factory.annotation.Autowired
    public TaskIdApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * GET /{task_Id}
     * Return task by task_Id from Database
     *
     * @param taskId (required)
     * @return Task was returned successfully (status code 200)
     * or Error while retrieving task - task_Id not found (status code 404)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<TaskModel> taskIdGet(UUID taskId) {
        TaskModel restModel = null;

        try{
            restModel = taskservice.getTaskById(taskId);
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(restModel);
    }

    /**
     * POST /{task_Id}
     * Update task in Database
     *
     * @param taskId    (required)
     * @param taskModel Task object (required)
     * @return New task was updated successfully (status code 200)
     * or Error while updating task - invalid task model (status code 400)
     * or Error while updating task - task_id not found (status code 404)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<ResponseModel> taskIdPost(UUID taskId, @Valid TaskModel taskModel) {
        return null;
    }

    /**
     * DELETE /{task_Id}
     * Delete task from Database
     *
     * @param taskId (required)
     * @return Task was deleted successfully (status code 200)
     * or Error while deleting task - task_id not found (status code 404)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<TaskModel> taskIdDelete(UUID taskId) {
        TaskModel restModel = null;

        try{
            restModel = taskservice.deleteTask(taskId);
        }catch(NoSuchElementException e){
           return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(restModel);
    }
}
