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
@WebMvcTest(GroupApiController.class)
public class GroupApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    IManagementService managementService;

    @Test
    public void testGroupStartPut() throws Exception {

        String groupId = "GROUPID";

        doNothing().when(managementService).resumeGroup(anyString());

        mvc.perform(put("/api/management/group/" + groupId + "/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).resumeGroup(groupId);
    }

    @Test
    public void testGroupStartPut_PauseException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new PauseException("")).when(managementService).resumeGroup(anyString());

        mvc.perform(put("/api/management/group/" + groupId + "/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).resumeGroup(groupId);
    }

    @Test
    public void testGroupStartPut_TransactionException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new TransactionRetriesExceeded("")).when(managementService).resumeGroup(anyString());

        mvc.perform(put("/api/management/group/" + groupId + "/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).resumeGroup(groupId);
    }

    @Test
    public void testGroupStartPut_InterruptedException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new InterruptedException("")).when(managementService).resumeGroup(anyString());

        mvc.perform(put("/api/management/group/" + groupId + "/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(managementService, only()).resumeGroup(groupId);
    }

    @Test
    public void testGroupStopPut() throws Exception {

        String groupId = "GROUPID";

        doNothing().when(managementService).pauseGroup(anyString(), any(OffsetDateTime.class));

        mvc.perform(put("/api/management/group/" + groupId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseGroup(anyString(), any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_noResumeDate() throws Exception {

        String groupId = "GROUPID";

        doNothing().when(managementService).pauseGroup(anyString(), any(OffsetDateTime.class));

        mvc.perform(put("/api/management/group/" + groupId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseGroup(groupId, null);
    }

    @Test
    public void testGroupStopPut_PauseException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new PauseException("")).when(managementService).pauseGroup(anyString(), any(OffsetDateTime.class));

        mvc.perform(put("/api/management/group/" + groupId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseGroup(anyString(), any(OffsetDateTime.class));
    }

    @Test
    public void testGroupStopPut_TransactionException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new TransactionRetriesExceeded("")).when(managementService).pauseGroup(anyString(), any(OffsetDateTime.class));

        mvc.perform(put("/api/management/group/" + groupId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseGroup(anyString(), any(OffsetDateTime.class));
    }

    @Test
    public void testGroupStopPut_InterruptedException() throws Exception {

        String groupId = "GROUPID";

        doThrow(new InterruptedException("")).when(managementService).pauseGroup(anyString(), any(OffsetDateTime.class));

        mvc.perform(put("/api/management/group/" + groupId + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isInternalServerError());

        verify(managementService, only()).pauseGroup(anyString(), any(OffsetDateTime.class));
    }
}
