package com.honeybadgers.cleaner.services;


import com.honeybadgers.cleaner.repository.LockRepository;
import com.honeybadgers.models.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(ScheduledConfigTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ScheduledServicesTest {

    @Autowired
    ScheduledServices service;

    @MockBean
    LockRepository lockRepository;

    ArrayList<RedisLock> list;
    RedisLock paraLock;

    @Before
    public void createMocks() {
        RedisLock lock1 = new RedisLock();
        lock1.setResume_date(LocalDateTime.now().plusMinutes(10));
        lock1.setId("1");

        RedisLock lock2 = new RedisLock();
        lock2.setResume_date(LocalDateTime.now().minusMinutes(1));
        lock2.setId("2");

        RedisLock lock3 = new RedisLock();
        lock3.setId("3");

        paraLock = new RedisLock();
        paraLock.setId("GROUP_PREFIX_PARLELLISM_CURRENT_TASKS_RUNNING_FOR_GROUP:HALLO");
        paraLock.setResume_date(LocalDateTime.now().minusMinutes(1));

        list = new ArrayList<RedisLock>();
        list.add(lock1);
        list.add(lock2);
        list.add(lock3);
        list.add(paraLock);

        when(lockRepository.findAll()).thenReturn((Iterable<RedisLock>) list);
        doNothing().when(lockRepository).delete(any());
    }

    @Test
    public void testSchedulerCleaner_twoIterations() throws InterruptedException {
        verify(lockRepository,never()).findAll();
        verify(lockRepository,never()).delete(any(RedisLock.class));

        // wait for initial delay
        Thread.sleep(200);

        verify(lockRepository).findAll();
        verify(lockRepository, times(1)).delete(list.get(1));
        verify(lockRepository, never()).delete(paraLock);

        RedisLock lockNew = new RedisLock();
        lockNew.setResume_date(LocalDateTime.now().minusMinutes(1));
        lockNew.setId("new");

        list = new ArrayList<RedisLock>();
        list.add(lockNew);

        when(lockRepository.findAll()).thenReturn((Iterable<RedisLock>) list);

        Thread.sleep(1000);

        verify(lockRepository, times(1)).delete(lockNew);
        verify(lockRepository, never()).delete(paraLock);
    }
}
