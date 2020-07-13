package com.honeybadgers.groupapi.controllers;

import com.honeybadgers.groupapi.models.GroupModel;
import com.honeybadgers.groupapi.models.ResponseModel;
import com.honeybadgers.groupapi.service.IGroupConvertUtils;
import com.honeybadgers.groupapi.service.IGroupService;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T15:31:54.117+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Group Api.base-path:/api/group}")
public class DefaultApiController implements DefaultApi {

    @Autowired
    IGroupService groupService;
    @Autowired
    IGroupConvertUtils convertUtils;

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
     * POST
     *
     * Create new group and save it in Database.
     * @param groupModel new group object (required)
     * @return New Group created successfully (status code 200)
     * or Error while creating Group - invalid Group model (status code 400)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<ResponseModel> rootPost(@Valid GroupModel groupModel) {
        ResponseModel response = new ResponseModel();
        response.setCode("200");
        response.setMessage("Success");

        try {
            groupService.createGroup(groupModel);
            logger.info("Group " + groupModel.getId() + " created.");
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


    /**
     * GET
     *
     * Get all Groups from Database.
     * @return List of Groups (status code 200)
     * or Unauthorized (status code 401)
     */
    @Override
    public ResponseEntity<List<GroupModel>> rootGet() {
        List<Group> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups.stream().map(group -> convertUtils.groupJpaToRest(group)).collect(Collectors.toList()));
    }
}
