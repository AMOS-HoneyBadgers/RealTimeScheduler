package com.honeybadgers.managementapi.service;


import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.impl.ManagementService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagementService.class)
public class ManagementServiceTest {
    @MockBean
    StateRepository stateRepository;

    @Autowired
    ManagementService service;
    @Test
    public void pauseScheduler() {

    }

    @Test
    public void resumeScheduler() {
    }

    @Test
    public void pauseTask() {
    }

    @Test
    public void resumeTask() {
    }
}