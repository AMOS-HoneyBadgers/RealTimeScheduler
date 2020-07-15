package com.honeybadgers.managementapi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

//@RunWith(SpringJUnit4ClassRunner.class)
//@WebMvcTest(SchedulerApiController.class)
public class SchedulerApiControllerTest {

    // Reason: WebMvc tries to init rabbit -> disable autoconfig -> fails to init ICommunication

    /*@Autowired
    private MockMvc mvc;

    @MockBean
    IManagementService managementService;

    @Test
    public void testSchedulerStartPut() throws Exception {

        mvc.perform(put( "/api/management/scheduler/start")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).resumeScheduler();
    }

    @Test
    public void testSchedulerStopPut() throws Exception {

        doThrow(new LockException("")).when(managementService).pauseScheduler(any(OffsetDateTime.class));

        mvc.perform(put( "/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"resume_date_time\":\"2020-06-06T22:08:37.901421500+02:00\"}"))
                .andExpect(status().isBadRequest());

        verify(managementService, only()).pauseScheduler(any(OffsetDateTime.class));
    }

    @Test
    public void testSchedulerStopPut_noRequestBody() throws Exception {

        mvc.perform(put( "/api/management/scheduler/stop")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(managementService, only()).pauseScheduler(null);
    }*/
}
