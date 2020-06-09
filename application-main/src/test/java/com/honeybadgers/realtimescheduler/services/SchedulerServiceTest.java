package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.honeybadgers.realtimescheduler.services.impl.SchedulerService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        RedisLock lockObj = new RedisLock();
        lockObj.setId(lockId);
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.of(lockObj));

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
        RedisLock lockObj = new RedisLock();
        lockObj.setId(lockId);
        when(lockRedisRepository.findById(lockId)).thenReturn(Optional.of(lockObj));

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
        RedisLock lockObj = new RedisLock();
        lockObj.setId(LOCKREDIS_SCHEDULER_ALIAS);
        when(lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS)).thenReturn(Optional.of(lockObj));

        assertTrue(service.isSchedulerLocked());
    }

    @Test
    public void testIsSchedulerLocked_Locked() {
        when(lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS)).thenReturn(Optional.empty());

        assertFalse(service.isSchedulerLocked());
    }


    @Test(expected = RuntimeException.class)
    public void sendTasksToDispatcherCantFindCapacityThrowsRuntimeException() {
        SchedulerService spy = spy(service);
        when(lockRedisRepository.findById(any())).thenReturn(null);
        spy.sendTaskstoDispatcher(any());

    }

    @Test
    public void sendTasksToDispatcher() {
        RedisLock test = new RedisLock();
        test.setId("ass");
        test.setCapacity(1);

        RedisTask task1 = new RedisTask();
        task1.setId("123");
        task1.setPriority(5);
        List<RedisTask> tasks = new ArrayList<RedisTask>();
        tasks.add(task1);

        SchedulerService spy = spy(service);
        when(lockRedisRepository.findById(any())).thenReturn(Optional.of(test));
        spy.sendTaskstoDispatcher(tasks);

        assertEquals(test.getCapacity(), 0);
        verify(lockRedisRepository).save(any());
        verify(sender).sendTaskToDispatcher(task1.getId());
    }
}