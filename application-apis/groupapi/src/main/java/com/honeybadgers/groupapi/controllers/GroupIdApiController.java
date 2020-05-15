package com.honeybadgers.groupapi.controllers;

import com.honeybadgers.groupapi.models.GroupModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-15T15:31:54.117+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Group Api.base-path:/api/group}")
public class GroupIdApiController implements GroupIdApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public GroupIdApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<GroupModel> groupIdIdGet(Long groupId) {
        return null;
    }
}
