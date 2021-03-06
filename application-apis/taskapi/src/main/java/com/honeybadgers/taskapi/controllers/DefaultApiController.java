package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.taskapi.models.ResponseModel;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:25.874+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Task Api.base-path:/api/task}")
public class DefaultApiController implements DefaultApi {

    @Autowired
    ITaskService taskService;

    private final NativeWebRequest request;

    static final Logger logger = LogManager.getLogger(DefaultApiController.class);


    @org.springframework.beans.factory.annotation.Autowired
    public DefaultApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * GET /
     * Get all tasks stored in Database
     *
     * @return OK - List of tasks (status code 200)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<List<TaskModel>> rootGet() {
        List<TaskModel> list = null;
        try {
            list = taskService.getAllTasks();
        } catch (InterruptedException e) {
            logger.error(Arrays.deepToString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (TransactionRetriesExceeded e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Creation of new Task
     *
     * @param taskModel new task object (required)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> rootPost(@Valid TaskModel taskModel) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            taskService.createTask(taskModel);
            logger.info("Task " + taskModel.getId() + " received.");
            if (taskModel.getForce() != null && taskModel.getForce()) {
                taskService.sendTaskToPriorityQueue(taskModel);
                logger.info("Task " + taskModel.getId() + " was immediately dispatched");
            }else
                taskService.sendTaskToTaskEventQueue(taskModel.getId().toString());

        } catch (UnknownEnumException e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (JpaException | CreationException | TransactionRetriesExceeded e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (InterruptedException e) {
            response.setCode("500");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
