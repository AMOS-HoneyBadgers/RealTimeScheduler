package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.models.model.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.ResponseModel;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:25.874+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Task Api.base-path:/api/task}")
public class TaskIdApiController implements TaskIdApi {

    @Autowired
    ITaskService taskService;

    private final NativeWebRequest request;

    final static Logger logger = LogManager.getLogger(TaskIdApiController.class);


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
    public ResponseEntity<TaskModel> taskIdGet(String taskId) {
        try{
            TaskModel restModel = taskService.getTaskById(taskId);
            return ResponseEntity.ok(restModel);
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<ResponseModel> taskIdPost(String taskId, @Valid TaskModel taskModel) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            taskService.updateTask(taskId, taskModel);
            logger.info("Task " + taskId + " updated.");
            //TODO: Behavior on changing force attributed. Task iight be scheduler by now. Dispatch once!
            if (taskModel.getForce() != null && taskModel.getForce()){
                taskService.sendTaskToPriorityQueue(taskModel);
            }else{
                taskService.sendTaskToTaskEventQueue(taskModel.getId().toString());
            }

        } catch (UnknownEnumException e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (JpaException | CreationException | IllegalStateException e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
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
    public ResponseEntity<TaskModel> taskIdDelete(String taskId) {
        try{
            TaskModel restModel = taskService.deleteTask(taskId);
            logger.info("Task " + taskId + " deleted.");
            return ResponseEntity.ok(restModel);
        }catch(NoSuchElementException e){
           return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> batchCreate (@RequestBody @Valid List<TaskModel> taskModels){

        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            for(TaskModel taskModel : taskModels){
                taskService.createTask(taskModel);
                logger.info("Task " + taskModel.getId() + " received.");
                if (taskModel.getForce() != null && taskModel.getForce()) {
                    taskService.sendTaskToPriorityQueue(taskModel);
                    logger.info("Task " + taskModel.getId() + " was immediately dispatched");
                }
            }
            taskService.sendTaskToTaskEventQueue("bulk");

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
