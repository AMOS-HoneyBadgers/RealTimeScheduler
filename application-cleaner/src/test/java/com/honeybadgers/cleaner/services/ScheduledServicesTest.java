package com.honeybadgers.cleaner.services;


import com.honeybadgers.cleaner.repository.LockRedisRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(ScheduledConfigTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ScheduledServicesTest {

    @Autowired
    ScheduledServices service;

    @MockBean
    LockRedisRepository lockRedisRepository;

    ArrayList<RedisLock> list;
    RedisLock paraLock;

    @Before
    public void createMocks() {
        RedisLock lock1 = new RedisLock();
        lock1.setResume_date(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10));
        lock1.setId("1");

        RedisLock lock2 = new RedisLock();
        lock2.setResume_date(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
        lock2.setId("2");

        RedisLock lock3 = new RedisLock();
        lock3.setId("3");

        paraLock = new RedisLock();
        paraLock.setId("GROUP_PREFIX_PARLELLISM_CURRENT_TASKS_RUNNING_FOR_GROUP:HALLO");
        paraLock.setResume_date(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));

        list = new ArrayList<RedisLock>();
        list.add(lock1);
        list.add(lock2);
        list.add(lock3);
        list.add(paraLock);

        when(lockRedisRepository.findAll()).thenReturn((Iterable<RedisLock>) list);
        doNothing().when(lockRedisRepository).deleteById(any());
    }

    @Test
    public void testSchedulerCleaner_twoIterations() throws InterruptedException {
        verify(lockRedisRepository,never()).findAll();
        verify(lockRedisRepository,never()).deleteById(anyString());

        // wait for initial delay
        Thread.sleep(200);

        verify(lockRedisRepository).findAll();
        verify(lockRedisRepository, times(1)).deleteById(list.get(1).getId());
        verify(lockRedisRepository, never()).deleteById(paraLock.getId());

        RedisLock lockNew = new RedisLock();
        lockNew.setResume_date(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
        lockNew.setId("new");

        list = new ArrayList<RedisLock>();
        list.add(lockNew);

        when(lockRedisRepository.findAll()).thenReturn((Iterable<RedisLock>) list);

        Thread.sleep(1000);

        verify(lockRedisRepository, times(1)).deleteById(lockNew.getId());
        verify(lockRedisRepository, never()).deleteById(paraLock.getId());
    }
}
