package com.honeybadgers.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T16:49:27.805+02:00[Europe/Berlin]")

@Controller
@RequestMapping("${openapi.Realtimescheduler Monitoring Api.base-path:/api/monitoring}")
public class GroupApiController implements GroupApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public GroupApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
