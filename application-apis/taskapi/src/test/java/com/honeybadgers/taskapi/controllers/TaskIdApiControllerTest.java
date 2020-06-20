package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskService;
import com.honeybadgers.taskapi.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void testPostTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel testModel = new TaskModel();
        testModel.setId(taskId);
        testModel.setGroupId("TestGruppe");

        mvc.perform(post("/api/task/" + taskId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.convertObjectToJsonBytes(testModel)))
            .andExpect(status().isOk())
            .andReturn();

        verify(taskservice).updateTask(taskId, testModel);
        verify(taskservice).sendTaskToTaskEventQueue(Mockito.anyString());
    }

    @Test
    public void testPostTaskById_Exeption() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel testModel = new TaskModel();
        testModel.setId(taskId);
        testModel.setGroupId("TestGruppe");

        when(taskservice.updateTask(taskId, testModel)).thenThrow(JpaException.class);

         mvc.perform(post("/api/task/" + taskId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtils.convertObjectToJsonBytes(testModel)))
            .andExpect(status().isBadRequest())
            .andReturn();

        verify(taskservice).updateTask(taskId, testModel);
        verify(taskservice, never()).sendTaskToTaskEventQueue(Mockito.anyString());
    }


    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskservice.getTaskById(taskId)).thenThrow(new NoSuchElementException());

        mvc.perform(get("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskservice).getTaskById(taskId);
    }

    @Test
    public void testDeleteTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskservice.deleteTask(taskId)).thenReturn(new TaskModel().id(taskId));

        MvcResult mvcResult = mvc.perform(delete("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(taskservice).deleteTask(taskId);

        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains(taskId.toString()));
    }

    @Test
    public void testDeleteTaskByIdNotFound() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskservice.deleteTask(taskId)).thenThrow(new NoSuchElementException());

         mvc.perform(delete("/api/task/" + taskId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskservice).deleteTask(taskId);
    }

}