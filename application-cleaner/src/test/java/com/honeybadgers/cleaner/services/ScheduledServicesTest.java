package com.honeybadgers.cleaner.services;


import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
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

import static org.mockito.Mockito.*;

@SpringJUnitConfig(ScheduledConfigTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ScheduledServicesTest {

    @Autowired
    ScheduledServices service;

    @MockBean
    PausedRepository pausedRepository;

    ArrayList<Paused> list;


    @Before
    public void createMocks() {
        Paused lock1 = new Paused();
        lock1.setResumeDate(Timestamp.from(Instant.now().plusSeconds(600)));
        lock1.setId("1");

        Paused lock2 = new Paused();
        lock2.setResumeDate(Timestamp.from(Instant.now().minusSeconds(60)));
        lock2.setId("2");

        Paused lock3 = new Paused();
        lock3.setId("3");


        list = new ArrayList<Paused>();
        list.add(lock1);
        list.add(lock2);
        list.add(lock3);


        when(pausedRepository.findAll()).thenReturn(list);
        doNothing().when(pausedRepository).deleteById(any());
    }

    @Test
    public void testSchedulerCleaner_twoIterations() throws InterruptedException {
        verify(pausedRepository,never()).findAll();
        verify(pausedRepository,never()).deleteById(anyString());

        // wait for initial delay
        Thread.sleep(200);

        verify(pausedRepository).findAll();
        verify(pausedRepository, times(1)).deleteById(list.get(1).getId());

        Paused lockNew = new Paused();
        lockNew.setResumeDate(Timestamp.from(Instant.now().minusSeconds(60)));
        lockNew.setId("new");

        list = new ArrayList<Paused>();
        list.add(lockNew);

        when(pausedRepository.findAll()).thenReturn(list);

        Thread.sleep(1000);

        verify(pausedRepository, times(1)).deleteById(lockNew.getId());
    }
}
