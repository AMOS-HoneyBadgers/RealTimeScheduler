package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.models.DateTimeBody;
import com.honeybadgers.managementapi.service.IManagementService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringJUnit4ClassRunner.class)
//@WebMvcTest(TaskApiController.class)
public class TaskApiControllerTest {

    // Reason: WebMvc tries to init rabbit -> disable autoconfig -> fails to init ICommunication

    /*@Autowired
    private MockMvc mvc;

    @MockBean
    IManagementService managementService;

    @Test
    public void testSchedulerStartPut() throws Exception {

        UUID taskId = UUID.randomUUID();

        mvc.perform(put( "/api/management/task/" + taskId.toString() + "/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).resumeTask(taskId);
    }

    @Test
    public void testSchedulerStopPut() throws Exception {

        UUID taskId = UUID.randomUUID();

        doThrow(new LockException("")).when(managementService).pauseTask(any(UUID.class), any(OffsetDateTime.class));

        mvc.perform(put( "/api/management/task/" + taskId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseTask(any(UUID.class), any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_noRequestBody() throws Exception {

        UUID taskId = UUID.randomUUID();

        mvc.perform(put( "/api/management/task/" + taskId.toString() + "/stop")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseTask(taskId, null);
    }*/
}
