package com.honeybadgers.managementapi.service;


import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.managementapi.exception.PauseException;
import com.honeybadgers.managementapi.service.impl.ManagementService;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.model.jpa.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.hibernate.TransactionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.honeybadgers.models.model.Constants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ManagementService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ManagementServiceTest {

    @MockBean
    PausedRepository pausedRepository;

    @MockBean
    ICommunication sender;

    @Autowired
    ManagementService service;

    @Test
    public void testPauseScheduler() {
        Paused lockObj = new Paused();
        lockObj.setId(PAUSED_SCHEDULER_ALIAS);
        lockObj.setResumeDate(null);

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenReturn(Optional.of(lockObj));
        assertDoesNotThrow(() -> service.pauseScheduler(null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseScheduler_withResumeDate() {
        Paused lockObj = new Paused();
        lockObj.setId(PAUSED_SCHEDULER_ALIAS);
        lockObj.setResumeDate(Timestamp.from(Instant.now()));

        Mockito.when(pausedRepository.insertCustomQuery(anyString(), any())).thenReturn(Optional.of(lockObj));
        assertDoesNotThrow(() -> service.pauseScheduler(OffsetDateTime.now()));
        verify(pausedRepository, times(1)).insertCustomQuery(anyString(), any());
        verify(pausedRepository, never()).insertCustomQueryNoTimestamp(anyString());
    }

    @Test
    public void testPauseScheduler_locked() {
        Paused lockObj = new Paused();
        lockObj.setId(PAUSED_SCHEDULER_ALIAS);
        lockObj.setResumeDate(null);

        DataIntegrityViolationException exception = new DataIntegrityViolationException("constraint [paused_pkey]");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);
        assertThrows(PauseException.class, () -> service.pauseScheduler(null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseScheduler_transactionException() {
        Paused lockObj = new Paused();
        lockObj.setId(PAUSED_SCHEDULER_ALIAS);
        lockObj.setResumeDate(null);

        TransactionException exception = new TransactionException("");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);
        assertThrows(TransactionRetriesExceeded.class, () -> service.pauseScheduler(null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testResumeScheduler() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        when(pausedRepository.deleteByIdCustomQuery(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.of(new Paused()));

        service.resumeScheduler();

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(PAUSED_SCHEDULER_ALIAS);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testResumeScheduler_transactionException() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        when(pausedRepository.deleteByIdCustomQuery(PAUSED_SCHEDULER_ALIAS)).thenThrow(new TransactionException(""));

        assertThrows(TransactionRetriesExceeded.class, () -> service.resumeScheduler());

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(PAUSED_SCHEDULER_ALIAS);
        verify(sender, never()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testPauseTask() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenReturn(Optional.of(lockObj));
        assertDoesNotThrow(() -> service.pauseTask(taskId.toString(), null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseTask_withResumeDate() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(Timestamp.from(Instant.now()));

        Mockito.when(pausedRepository.insertCustomQuery(anyString(), any())).thenReturn(Optional.of(lockObj));
        assertDoesNotThrow(() -> service.pauseTask(taskId.toString(), OffsetDateTime.now()));
        verify(pausedRepository, times(1)).insertCustomQuery(anyString(), any());
        verify(pausedRepository, never()).insertCustomQueryNoTimestamp(anyString());
    }

    @Test
    public void testPauseTask_locked() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        DataIntegrityViolationException exception = new DataIntegrityViolationException("constraint [paused_pkey]");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);
        assertThrows(PauseException.class, () -> service.pauseTask(taskId.toString(), null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseTask_transactionException() {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        TransactionException exception = new TransactionException("");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);
        assertThrows(TransactionRetriesExceeded.class, () -> service.pauseTask(taskId.toString(), null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testResumeTask() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();

        when(pausedRepository.deleteByIdCustomQuery(lockId)).thenReturn(Optional.of(new Paused()));

        service.resumeTask(taskId.toString());

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(lockId);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testResumeTask_transactionException() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        UUID taskId = UUID.randomUUID();
        String lockId = PAUSED_TASK_PREFIX + taskId.toString();

        when(pausedRepository.deleteByIdCustomQuery(lockId)).thenThrow(new TransactionException(""));

        assertThrows(TransactionRetriesExceeded.class, () -> service.resumeTask(taskId.toString()));

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(lockId);
        verify(sender, never()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testPauseGroup() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenReturn(Optional.of(lockObj));

        assertDoesNotThrow(() -> service.pauseGroup(groupId, null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseGroup_withResumeDate() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(Timestamp.from(Instant.now()));

        Mockito.when(pausedRepository.insertCustomQuery(anyString(), any())).thenReturn(Optional.of(lockObj));
        assertDoesNotThrow(() -> service.pauseTask(groupId, OffsetDateTime.now()));
        verify(pausedRepository, times(1)).insertCustomQuery(anyString(), any());
        verify(pausedRepository, never()).insertCustomQueryNoTimestamp(anyString());
    }

    @Test
    public void testPauseGroup_locked() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        DataIntegrityViolationException exception = new DataIntegrityViolationException("constraint [paused_pkey]");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);

        assertThrows(PauseException.class, () -> service.pauseGroup(groupId, null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testPauseGroup_transactionException() {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;
        Paused lockObj = new Paused();
        lockObj.setId(lockId);
        lockObj.setResumeDate(null);

        TransactionException exception = new TransactionException("");

        Mockito.when(pausedRepository.insertCustomQueryNoTimestamp(lockObj.getId())).thenThrow(exception);
        assertThrows(TransactionRetriesExceeded.class, () -> service.pauseGroup(groupId, null));
        verify(pausedRepository, times(1)).insertCustomQueryNoTimestamp(anyString());
        verify(pausedRepository, never()).insertCustomQuery(anyString(), any());
    }

    @Test
    public void testResumeGroup() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;

        when(pausedRepository.deleteByIdCustomQuery(lockId)).thenReturn(Optional.of(new Paused()));

        service.resumeGroup(groupId);

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(lockId);
        verify(sender, only()).sendTaskToTasksQueue(any());
    }

    @Test
    public void testResumeGroup_transactionException() throws InterruptedException, PauseException, TransactionRetriesExceeded {
        String groupId = "GROUPID";
        String lockId = PAUSED_GROUP_PREFIX + groupId;

        when(pausedRepository.deleteByIdCustomQuery(lockId)).thenThrow(new TransactionException(""));

        assertThrows(TransactionRetriesExceeded.class, () -> service.resumeGroup(groupId));

        Mockito.verify(pausedRepository, Mockito.only()).deleteByIdCustomQuery(lockId);
        verify(sender, never()).sendTaskToTasksQueue(any());
    }
}