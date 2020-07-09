package com.honeybadgers.managementapi.service;


import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.service.impl.ManagementService;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

import static com.honeybadgers.models.model.Constants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagementService.class)
public class ManagementServiceTest {

    @MockBean
    PausedRepository pausedRepository;

    @Autowired
    ManagementService service;

    @MockBean
    ICommunication sender;

    @Test
    public void testPauseScheduler() {
        Mockito.when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseScheduler(null));
    }

    @Test
    public void testPauseScheduler_locked() {
        Paused lockObj = new Paused();
        lockObj.setId(PAUSED_SCHEDULER_ALIAS);
        Mockito.when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseScheduler(null));
    }

    @Test
    public void testResumeScheduler() {
        service.resumeScheduler();
        Mockito.verify(pausedRepository, Mockito.only()).deleteById(PAUSED_SCHEDULER_ALIAS);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testPauseTask() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Mockito.when(pausedRepository.findById(lockId)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseTask(taskId.toString(), null));
    }

    @Test
    public void testPauseTask_locked() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        Mockito.when(pausedRepository.findById(lockId)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseTask(taskId.toString(), null));
    }

    @Test
    public void testResumeTask() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        service.resumeTask(taskId.toString());
        Mockito.verify(pausedRepository, Mockito.only()).deleteById(lockId);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testPauseGroup() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Mockito.when(pausedRepository.findById(lockId)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.pauseGroup(groupId, null));
    }

    @Test
    public void testPauseGroup_locked() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        Mockito.when(pausedRepository.findById(lockId)).thenReturn(Optional.of(lockObj));
        assertThrows(LockException.class, () -> service.pauseGroup(groupId, null));
    }

    @Test
    public void testResumeGroup() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        service.resumeGroup(groupId);
        Mockito.verify(pausedRepository, Mockito.only()).deleteById(lockId);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }
}