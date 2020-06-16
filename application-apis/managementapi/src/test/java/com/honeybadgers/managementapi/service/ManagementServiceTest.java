package com.honeybadgers.managementapi.service;


import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.repository.LockRedisRepository;
import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.impl.ManagementService;
import com.honeybadgers.models.model.RedisLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static com.honeybadgers.managementapi.service.impl.ManagementService.*;
import static com.honeybadgers.models.model.Constants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagementService.class)
public class ManagementServiceTest {

    @MockBean
    LockRedisRepository stateRepository;

    @Autowired
    ManagementService service;


    @Test
    public void testPauseScheduler() {
        Mockito.when(stateRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseScheduler(null));
    }

    @Test
    public void testPauseScheduler_locked() {
        RedisLock lockObj = new RedisLock();
        lockObj.setId(LOCK_SCHEDULER_ALIAS);
        Mockito.when(stateRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseScheduler(null));
    }

    @Test
    public void testResumeScheduler() {
        service.resumeScheduler();
        Mockito.verify(stateRepository, Mockito.only()).deleteById(LOCK_SCHEDULER_ALIAS);
    }

    @Test
    public void testPauseTask() {
        UUID taskId = UUID.randomUUID();
        String lockId = LOCK_TASK_PREFIX + taskId.toString();
        Mockito.when(stateRepository.findById(lockId)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseTask(taskId, null));
    }

    @Test
    public void testPauseTask_locked() {
        UUID taskId = UUID.randomUUID();
        String lockId = LOCK_TASK_PREFIX + taskId.toString();
        RedisLock lockObj = new RedisLock();
        lockObj.setId(lockId);
        Mockito.when(stateRepository.findById(lockId)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseTask(taskId, null));
    }

    @Test
    public void testResumeTask() {
        UUID taskId = UUID.randomUUID();
        String lockId = LOCK_TASK_PREFIX + taskId.toString();
        service.resumeTask(taskId);
        Mockito.verify(stateRepository, Mockito.only()).deleteById(lockId);
    }

    @Test
    public void testPauseGroup() {
        String groupId = "GROUPID";
        String lockId = LOCK_GROUP_PREFIX + groupId;
        Mockito.when(stateRepository.findById(lockId)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseGroup(groupId, null));
    }

    @Test
    public void testPauseGroup_locked() {
        String groupId = "GROUPID";
        String lockId = LOCK_GROUP_PREFIX + groupId;
        RedisLock lockObj = new RedisLock();
        lockObj.setId(lockId);
        Mockito.when(stateRepository.findById(lockId)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseGroup(groupId, null));
    }

    @Test
    public void testResumeGroup() {
        String groupId = "GROUPID";
        String lockId = LOCK_GROUP_PREFIX + groupId;
        service.resumeGroup(groupId);
        Mockito.verify(stateRepository, Mockito.only()).deleteById(lockId);
    }
}