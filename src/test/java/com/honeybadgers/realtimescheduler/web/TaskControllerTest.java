package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.services.PostgresExampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("postgre")              // set postgre profile as active, because TaskController is only initialized in postgre Profile
@WebMvcTest(TaskController.class)       // mock only the given controller (for mock all use @WebMvcTest & @AutoConfigureMockMvc)
public class TaskControllerTest {



    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostgresExampleService service;

    @Test
    public void testGetAllTasks() throws Exception {

        Task task = new Task(1L, 10, "testTask1", null, new Timestamp(System.currentTimeMillis()), null);

        List<Task> allTasks = Collections.singletonList(task);

        // specify mock result
        given(service.getAllTasks()).willReturn(allTasks);

        mvc.perform(get("/api/task/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(task.getName())));
    }
}
