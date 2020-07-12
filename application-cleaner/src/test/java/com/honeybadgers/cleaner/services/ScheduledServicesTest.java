package com.honeybadgers.cleaner.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(ScheduledConfigTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ScheduledServicesTest {

    @Autowired
    ScheduledServices service;

    @MockBean
    PausedRepository pausedRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    ICommunication sender;

    @Test
    public void testSchedulerCleaner_twoIterations() throws InterruptedException {
        verify(pausedRepository,never()).deleteAllExpired();

        // wait for initial delay
        Thread.sleep(200);

        Paused lock1 = new Paused();
        lock1.setResumeDate(Timestamp.from(Instant.now().plusSeconds(600)));
        lock1.setId("1");

        when(pausedRepository.deleteAllExpired()).thenReturn(new ArrayList<>());

        verify(pausedRepository, times(1)).deleteAllExpired();
        verify(sender, atMostOnce()).sendTaskToDispatcher(any());

        Paused lock2 = new Paused();
        lock1.setResumeDate(Timestamp.from(Instant.now().minusSeconds(600)));
        lock1.setId("1");

        Paused lock3 = new Paused();
        lock1.setResumeDate(Timestamp.from(Instant.now()));
        lock1.setId("1");

        when(pausedRepository.deleteAllExpired()).thenReturn(Arrays.asList(lock1, lock2, lock3));

        Thread.sleep(1000);

        verify(pausedRepository, times(2)).deleteAllExpired();
    }
}
