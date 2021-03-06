package com.honeybadgers.taskapi.controllers;


import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    public void testGetAllTasks() throws Exception {
        List<TaskModel> tasks = new LinkedList<TaskModel>();

        TaskModel taskmodel = new TaskModel();
        String uuid = UUID.randomUUID().toString();
        taskmodel.setId(uuid);
        taskmodel.setGroupId("TestGroup");
        taskmodel.setPriority(100);
        tasks.add(taskmodel);

        when(taskservice.getAllTasks()).thenReturn(tasks);

        MvcResult mvcResult = mvc.perform(get("/api/task/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

        verify(taskservice).getAllTasks();

        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("TestGroup"));
        assertTrue(response.contains(uuid));
    }


    @Test
    public void testTaskCreate() throws Exception {
        TaskModel testModel = new TaskModel();
        testModel.setId(UUID.randomUUID().toString());
        testModel.setGroupId("TestGruppe");

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isOk());

        verify(taskservice).createTask(any(TaskModel.class));
        verify(taskservice).sendTaskToTaskEventQueue(Mockito.anyString());
    }



    @Test
    public void testTaskCreate_JpaExceptionWasThrown() throws Exception {
        TaskModel testModel = new TaskModel();
        testModel.setId(UUID.randomUUID().toString());
        testModel.setGroupId("TestGruppe");

        JpaException ex = new JpaException("Primary or unique constraint failed!");
        when(taskservice.createTask(any(TaskModel.class))).thenThrow(ex);

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isBadRequest());

        verify(taskservice)
                .createTask(any(TaskModel.class));
    }

    @Test
    public void testTaskCreate_EnumExceptionWasThrown() throws Exception {
        TaskModel testModel = new TaskModel();
        testModel.setId(UUID.randomUUID().toString());
        testModel.setGroupId("TestGruppe");

        UnknownEnumException ex = new UnknownEnumException ("Invalid Enum");
        when(taskservice.createTask(any(TaskModel.class))).thenThrow(ex);

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isBadRequest());

        verify(taskservice)
                .createTask(any(TaskModel.class));
    }

    @Test
    public void testTaskCreate_CreationExceptionWasThrown() throws Exception {
        TaskModel testModel = new TaskModel();
        testModel.setId(UUID.randomUUID().toString());
        testModel.setGroupId("TestGruppe");

        CreationException ex = new CreationException("Group of task has other groups as children -> aborting!");
        when(taskservice.createTask(any(TaskModel.class))).thenThrow(ex);

        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(testModel)))
                .andExpect(status().isBadRequest());

        verify(taskservice)
                .createTask(any(TaskModel.class));
    }

    @Test
    public void testTaskCreate_invalidModel() throws Exception {
        mvc.perform(post( "/api/task/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{" +
                                "\"id\":\"" + UUID.randomUUID().toString() + "\"," +
                                "\"group_id\":\"TestGroup\"," +
                                "\"mode\":\"Solo\"" +
                        "}"
                ))
                .andExpect(status().isBadRequest());
    }
}
