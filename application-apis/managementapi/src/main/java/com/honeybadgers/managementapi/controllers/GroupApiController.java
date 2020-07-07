package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.models.DateTimeBody;
import com.honeybadgers.managementapi.models.ResponseModel;
import com.honeybadgers.managementapi.service.IManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T01:04:26.284+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Management Api.base-path:/api/management}")
public class GroupApiController implements GroupApi {

    @Autowired
    IManagementService managmentService;

    private final NativeWebRequest request;

    static final Logger logger = LogManager.getLogger(GroupApiController.class);


    @org.springframework.beans.factory.annotation.Autowired
    public GroupApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Unlocks a Group.
     * @param groupId  (required)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> groupGroupIdStartPut(String groupId) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        managmentService.resumeGroup(groupId);

        return ResponseEntity.ok(response);
    }

    /**
     * Locks a Group.
     * @param groupId  (required)
     * @param dateTimeBody DateTime body which indicates, when to resume scheduling (optional)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> groupGroupIdStopPut(String groupId, @Valid DateTimeBody dateTimeBody) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try{
            managmentService.pauseGroup(groupId, dateTimeBody != null ? dateTimeBody.getResumeDateTime() : null);
        }catch(LockException e){
            response.setCode("400");
            response.setMessage("Group with groupId=" + groupId + " already paused!");
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
