package com.honeybadgers.groupapi.controllers;

import com.honeybadgers.groupapi.exceptions.CreationException;
import com.honeybadgers.groupapi.exceptions.JpaException;
import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.ResponseModel;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.UnknownEnumException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T15:31:54.117+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Group Api.base-path:/api/group}")
public class DefaultApiController implements DefaultApi {

    @Autowired
    IGroupService groupService;

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
     * Create new group
     * @param groupModel new group object (required)
     * @return
     */
    @Override
    public ResponseEntity<ResponseModel> rootPost(@Valid GroupModel groupModel) {

        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            groupService.createGroup(groupModel);
            //groupService.sendGroupToTaskEventQueue(groupModel.getId());
        } catch (UnknownEnumException e) {
            // Should never happen, due to GroupModel being validated (validates, if model is of Enum)
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
