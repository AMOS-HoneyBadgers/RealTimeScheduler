package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.service.ITaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(TaskIdApiController.class)
public class TaskIdApiControllerTest {

    @MockBean
    ITaskService taskservice;

    @Autowired
    private MockMvc mvc;

    @Test
    public void taskIdGet() throws Exception {
        UUID taskId = UUID.randomUUID();
        mvc.perform(get("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}