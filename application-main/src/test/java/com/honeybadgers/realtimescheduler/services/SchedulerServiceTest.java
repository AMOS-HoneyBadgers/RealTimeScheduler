package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

import static com.honeybadgers.realtimescheduler.services.impl.SchedulerService.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SchedulerService.class)
public class SchedulerServiceTest {

    @MockBean
    private ITaskService taskService;

    @MockBean
    private TaskRedisRepository taskRedisRepository;

    @MockBean
    private LockRedisRepository lockRedisRepository;

    @MockBean

    private ICommunication sender;

    @Autowired
    private SchedulerService service;


    @Test
    public void testGetAllTasksAndSort() {
        // TODO IMPLEMENT @Christoff and @Stan
        service.getAllRedisTasksAndSort();
    }

    // TODO TEST ANPASSEN
    @Test(expected = RuntimeException.class)
    public void testScheduleTask() {
        Task t = new Task();
        t.setId("TEST");
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
        //verify(taskRedisRepository).save(any());

    }

    @Test
    public void testIsTaskLocked_NotLocked() {
        String taskId = UUID.randomUUID().toString();
        String lockId = LOCKREDIS_TASK_PREFIX + taskId;
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.of(lockId));

        assertTrue(service.isTaskLocked(taskId));
    }

    @Test
    public void testIsTaskLocked_Locked() {
        String taskId = UUID.randomUUID().toString();
        String lockId = LOCKREDIS_TASK_PREFIX + taskId;
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.empty());

        assertFalse(service.isTaskLocked(taskId));
    }

    @Test
    public void testIsGroupLocked_NotLocked() {
        String groupId = "GROUPID";
        String lockId = LOCKREDIS_GROUP_PREFIX + groupId;
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.of(lockId));

        assertTrue(service.isGroupLocked(groupId));
    }

    @Test
    public void testIsGroupLocked_Locked() {
        String groupId = "GROUPID";
        String lockId = LOCKREDIS_GROUP_PREFIX + groupId;
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.empty());

        assertFalse(service.isGroupLocked(groupId));
    }

    @Test
    public void testIsSchedulerLocked_NotLocked() {
        when(lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS)).thenReturn(Optional.of(LOCKREDIS_SCHEDULER_ALIAS));

        assertTrue(service.isSchedulerLocked());
    }

    @Test
    public void testIsSchedulerLocked_Locked() {
        when(lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS)).thenReturn(Optional.empty());

        assertFalse(service.isSchedulerLocked());
    }
}
