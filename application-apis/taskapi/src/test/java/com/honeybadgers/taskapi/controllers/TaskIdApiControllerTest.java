package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void testGetTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskservice.getTaskById(taskId)).thenReturn(new TaskModel().id(taskId));

        MvcResult mvcResult = mvc.perform(get("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(taskservice).getTaskById(taskId);

        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains(taskId.toString()));
    }

    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        mvc.perform(get("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTaskById(){

    }
}