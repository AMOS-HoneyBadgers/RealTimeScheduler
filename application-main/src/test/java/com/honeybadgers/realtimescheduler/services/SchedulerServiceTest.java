package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.impl.GroupService;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

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

    @MockBean
    private IGroupService groupService;

    @Autowired
    private SchedulerService service;

    @Test
    public void testGetAllTasksAndSort() {
        // TODO IMPLEMENT @Christoff and @Stan
        service.getAllRedisTasksAndSort();
    }

    // TODO TEST ANPASSEN
    @Test
    public void testScheduleTask() {
        Group group = createGroupTestObject();
        Task t = new Task();
        t.setId("TEST");
        t.setPriority(42);
        t.setGroup(group);


        RedisTask redisTask = new RedisTask();
        redisTask.setId(t.getId());
        RedisLock capacity = new RedisLock();
        capacity.setCurrentTasks(50);
        SchedulerService spy = spy(service);
        when(taskService.getTaskById(t.getId())).thenReturn(Optional.of(t));
        spy.scheduleTask(t.getId());

        verify(taskRedisRepository).save(any());
        verify(taskService).calculatePriority(t);
        verify(spy).getAllRedisTasksAndSort();

    }
    @Test(expected = RuntimeException.class)
    public void testIfTaskNotFoundThenThrow(){
        Task t = new Task();
        t.setId("TEST");
        t.setPriority(42);
        when(taskService.getTaskById(t.getId())).thenReturn(null);
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
    }
    @Test
    public void testIfSchedulerIsLockedDontSend(){
        Group group = createGroupTestObject();
        //if scheduler is locked then don't do anything
        Task t = new Task();
        t.setId("TEST");
        t.setPriority(42);
        t.setGroup(group);

        when(taskService.getTaskById(t.getId())).thenReturn(Optional.of(t));
        when(lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS)).thenReturn(Optional.of(new RedisLock()));
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
        verify(spy, times(0)).sendTaskstoDispatcher(any());
    }

   /* @Test
    public void testIfSchedulerGetsSpecialTrigger(){
        //if scheduler is locked then don't do anything
        Task t = new Task();
        t.setId("SPECIAL_TRIGGER");

        RedisLock capacity = new RedisLock();
        capacity.setCurrentTasks(50);
        when(lockRedisRepository.findById(dispatcherCapacityId)).thenReturn(Optional.of(capacity));

        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
        verify(spy).sendTaskstoDispatcher(any());
    }*/

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

    @Test
    public void sendTasksToDispatcher() {
        RedisLock test = new RedisLock();
        test.setId("ass");
        test.setCurrentTasks(0);

        RedisTask task1 = new RedisTask();
        task1.setId("123");
        task1.setPriority(5);
        List<RedisTask> tasks = new ArrayList<RedisTask>();
        tasks.add(task1);

        Group group = createGroupTestObject();

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(any())).thenReturn(group);
        when(lockRedisRepository.findById(any())).thenReturn(Optional.of(test));

        // mock everything related to isPaused
        when(lockRedisRepository.findById(LOCKREDIS_TASK_PREFIX + task1.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(task1.getId())).thenReturn(new ArrayList<>());

        doNothing().when(taskRedisRepository).deleteById(task1.getId());

        spy.sendTaskstoDispatcher(tasks);

        assertEquals(1, test.getCurrentTasks());
        verify(lockRedisRepository).save(any());
        verify(sender).sendTaskToDispatcher(task1.getId());
        verify(taskRedisRepository).deleteById(task1.getId());
    }

    @Test
    public void sendTasksToDispatcher_taskPaused() {
        RedisLock test = new RedisLock();
        test.setId("ass");
        test.setCurrentTasks(0);

        RedisTask task1 = new RedisTask();
        task1.setId("123");
        task1.setPriority(5);
        List<RedisTask> tasks = new ArrayList<RedisTask>();
        tasks.add(task1);

        RedisLock taskLock = new RedisLock();
        taskLock.setId(LOCKREDIS_TASK_PREFIX + task1.getId());

        SchedulerService spy = spy(service);
        when(lockRedisRepository.findById(any())).thenReturn(Optional.of(test));

        // mock everything related to isPaused
        when(lockRedisRepository.findById(taskLock.getId())).thenReturn(Optional.of(taskLock));
        when(taskService.getRecursiveGroupsOfTask(task1.getId())).thenReturn(new ArrayList<>());

        doNothing().when(taskRedisRepository).deleteById(task1.getId());

        spy.sendTaskstoDispatcher(tasks);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertEquals(0, test.getCurrentTasks());
        verify(lockRedisRepository, never()).save(any());
        verify(sender, never()).sendTaskToDispatcher(task1.getId());
        verify(taskRedisRepository, never()).deleteById(task1.getId());
    }

    @Test
    public void sendTasksToDispatcher_groupPaused() {
        RedisLock test = new RedisLock();
        test.setId("ass");
        test.setCurrentTasks(0);

        RedisTask task1 = new RedisTask();
        task1.setId("123");
        task1.setPriority(5);
        List<RedisTask> tasks = new ArrayList<RedisTask>();
        tasks.add(task1);

        RedisLock groupLock = new RedisLock();
        groupLock.setId(LOCKREDIS_GROUP_PREFIX + "testGroup");

        SchedulerService spy = spy(service);
        when(lockRedisRepository.findById(any())).thenReturn(Optional.of(test));

        // mock everything related to isPaused
        when(lockRedisRepository.findById(LOCKREDIS_TASK_PREFIX + task1.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(task1.getId())).thenReturn(new ArrayList<>(Collections.singleton("testGroup")));
        when(lockRedisRepository.findById(groupLock.getId())).thenReturn(Optional.of(groupLock));

        doNothing().when(taskRedisRepository).deleteById(task1.getId());

        spy.sendTaskstoDispatcher(tasks);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertEquals(0, test.getCurrentTasks());
        verify(lockRedisRepository, never()).save(any());
        verify(sender, never()).sendTaskToDispatcher(task1.getId());
        verify(taskRedisRepository, never()).deleteById(task1.getId());
    }

    private Group createGroupTestObject() {
        Group group = new Group();
        group.setId("456");
        group.setParallelismDegree(10);
        group.setParentGroup(null);
        return group;
    }
}