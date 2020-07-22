package com.honeybadgers.managementapi.controllers;

import com.honeybadgers.managementapi.exception.PauseException;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(SchedulerApiController.class)
public class SchedulerApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    IManagementService managementService;

    @Test
    public void testSchedulerStartPut() throws Exception {

        doNothing().when(managementService).resumeScheduler();

        mvc.perform(put("/api/management/scheduler/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).resumeScheduler();
    }

    @Test
    public void testSchedulerStartPut_PauseException() throws Exception {

        doThrow(new PauseException("")).when(managementService).resumeScheduler();

        mvc.perform(put("/api/management/scheduler/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).resumeScheduler();
    }

    @Test
    public void testSchedulerStartPut_TransactionException() throws Exception {

        doThrow(new TransactionRetriesExceeded("")).when(managementService).resumeScheduler();

        mvc.perform(put("/api/management/scheduler/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).resumeScheduler();
    }

    @Test
    public void testSchedulerStartPut_InterruptedException() throws Exception {

        doThrow(new InterruptedException("")).when(managementService).resumeScheduler();

        mvc.perform(put("/api/management/scheduler/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(managementService, only()).resumeScheduler();
    }

    @Test
    public void testSchedulerStopPut() throws Exception {

        doNothing().when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put("/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseScheduler(any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_noResumeDate() throws Exception {

        doNothing().when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put("/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseScheduler(null);
    }

    @Test
    public void testSchedulerStopPut_PauseException() throws Exception {

        doThrow(new PauseException("")).when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put("/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseScheduler(any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_TransactionException() throws Exception {

        doThrow(new TransactionRetriesExceeded("")).when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put("/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseScheduler(any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_InterruptedException() throws Exception {

        doThrow(new InterruptedException("")).when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put("/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isInternalServerError());

        verify(managementService, only()).pauseScheduler(any(OffsetDateTime.class));
    }
}
