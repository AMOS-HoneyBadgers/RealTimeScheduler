package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.models.ResponseModel;
import com.honeybadgers.managementapi.service.IManagmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:26.284+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Management Api.base-path:/api/management}")
public class SchedulerApiController implements SchedulerApi {

    private final NativeWebRequest request;

    @Autowired
    IManagmentService managmentService;

    @org.springframework.beans.factory.annotation.Autowired
    public SchedulerApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<ResponseModel> schedulerStartPut() {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try{
            managmentService.resumeScheduler();
        }catch(Exception e){

        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResponseModel> schedulerStopPut() {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try{
            managmentService.pauseScheduler();
        }catch(Exception e){

        }

        return ResponseEntity.ok(response);
    }
}
