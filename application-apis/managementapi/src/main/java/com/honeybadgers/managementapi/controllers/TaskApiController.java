package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.models.DateTimeBody;
import com.honeybadgers.managementapi.models.ResponseModel;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-06-05T20:22:57.974+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Management Api.base-path:/api/management}")
public class TaskApiController implements TaskApi {

    @Autowired
    IManagementService managmentService;

    private final NativeWebRequest request;


    @org.springframework.beans.factory.annotation.Autowired
    public TaskApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Unlocks a Task.
     * @param taskId  (required)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> taskTaskIdStartPut(String taskId) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            managmentService.resumeTask(taskId);
        } catch (InterruptedException e) {
            response.setCode("500");
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (TransactionRetriesExceeded e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (LockException e) {
            response.setMessage("Task was not paused!");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Locks a Task.
     * @param taskId  (required)
     * @param dateTimeBody DateTime body which indicates, when to resume scheduling (optional)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> taskTaskIdStopPut(String taskId, @Valid DateTimeBody dateTimeBody) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try{
            managmentService.pauseTask(taskId, dateTimeBody != null ? dateTimeBody.getResumeDateTime() : null);
        } catch(LockException e){
            response.setCode("400");
            response.setMessage("Task with taskId=" + taskId.toString() + " already paused!");
            return ResponseEntity.badRequest().body(response);
        } catch (InterruptedException e) {
            response.setCode("500");
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (TransactionRetriesExceeded e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
