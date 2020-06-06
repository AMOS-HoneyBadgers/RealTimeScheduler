package com.honeybadgers.managementapi.service;


import com.honeybadgers.managementapi.service.impl.ManagementService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagementService.class)
public class ManagementServiceTest {

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