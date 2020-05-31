package com.honeybadgers.taskapi.controllers;


import com.honeybadgers.taskapi.configuration.PostgreConfig;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import com.honeybadgers.taskapi.service.impl.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(DefaultApiController.class)
public class DefaultApiControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    ITaskService taskservice;


    @Test
    public void taskCreateTest() throws Exception {
        TaskModel testModel = new TaskModel();
        testModel.setId(UUID.randomUUID());
        testModel.setGroupId("TestGruppe");

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                 .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isOk());

        verify(taskservice, only())
        .createTask(any(TaskModel.class));
    }

    @Test
    public void  taskCreateInvalidModel() throws Exception {

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"id\":\""  + UUID.randomUUID().toString() + "\"," +
                        " \"group_id\":\"TestGroup\","  +
                         "\"mode\":\"Solo\"}"))
                .andExpect(status().isBadRequest());
    }

    
}
