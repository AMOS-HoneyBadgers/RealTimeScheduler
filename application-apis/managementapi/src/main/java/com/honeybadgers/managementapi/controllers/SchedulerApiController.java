package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.exception.PauseException;
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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:26.284+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Management Api.base-path:/api/management}")
public class SchedulerApiController implements SchedulerApi {

    @Autowired
    IManagementService managmentService;

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public SchedulerApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Resumes the Scheduler.
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> schedulerStartPut() {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            managmentService.resumeScheduler();
        } catch (InterruptedException e) {
            response.setCode("500");
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (TransactionRetriesExceeded e) {
            response.setCode("400");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (PauseException e) {
            response.setMessage("Scheduler was not paused!");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Pauses the Scheduler.
     * @param dateTimeBody DateTime body which indicates, when to resume scheduling (optional)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> schedulerStopPut(@Valid DateTimeBody dateTimeBody) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try{
            managmentService.pauseScheduler(dateTimeBody != null ? dateTimeBody.getResumeDateTime() : null);
        } catch(PauseException e){
            response.setCode("400");
            response.setMessage("Scheduler already paused!");
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
