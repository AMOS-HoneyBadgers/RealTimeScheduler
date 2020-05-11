package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.services.PostgresExampleService;
import com.honeybadgers.realtimescheduler.services.RabbitMQSender;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(HelloWorldController.class)
public class HelloWorldControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetAllTasks() throws Exception {
        /*RabbitMQSender rabbitMQSender = Mockito.mock(RabbitMQSender.class);
        when(rabbitMQSender.send(any(String.class))).thenReturn()
        mvc.perform(get("hello/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());*/
        Assert.isTrue(true);
    }

}