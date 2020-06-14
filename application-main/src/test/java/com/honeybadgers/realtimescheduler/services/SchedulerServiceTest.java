package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.*;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.impl.GroupService;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
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
    public void testIfTaskNotFoundThenThrow() {
        Task t = new Task();
        t.setId("TEST");
        t.setPriority(42);
        when(taskService.getTaskById(t.getId())).thenReturn(null);
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
    }

    @Test
    public void testIfSchedulerIsLockedDontSend() {
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
        test.setCurrentTasks(0);

        RedisTask task1 = new RedisTask();
        task1.setId("123");
        task1.setPriority(5);
        List<RedisTask> tasks = new ArrayList<RedisTask>();
        tasks.add(task1);

        Group group = createGroupTestObject();
        Task task = new Task();
        task.setId("TEST");
        task.setGroup(group);
        task.setActiveTimeFrames(null);

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(any())).thenReturn(group);
        when(lockRedisRepository.findById(any())).thenReturn(Optional.of(test));
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task));

        spy.sendTaskstoDispatcher(tasks);

        assertEquals(test.getCurrentTasks(), 1);
        verify(lockRedisRepository).save(any());
        verify(sender).sendTaskToDispatcher(task1.getId());
    }

    private Group createGroupTestObject() {
        Group group = new Group();
        group.setId("456");
        group.setParallelismDegree(10);
        group.setParentGroup(null);
        return group;
    }

    @Test
    public void testGetActiveTimesForTask_OnlyTaskHasActiveTimes() {

        Task task = new Task();
        task.setId("TEST");
        List<ActiveTimes> activeTimes = new ArrayList<ActiveTimes>();
        task.setActiveTimeFrames(activeTimes);
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        task.setGroup(parentGroup);
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        Assert.assertEquals(spy.getActiveTimesForTask(task), activeTimes);
    }

    @Test
    public void testGetActiveTimesForTask_TaskAndParentHaveActiveTimes() {
        //prepare Task
        Task task = new Task();
        task.setId("TEST");
        List<ActiveTimes> activeTimes = new ArrayList<ActiveTimes>();
        ActiveTimes taskaktivetime = new ActiveTimes();
        taskaktivetime.setFrom(new Time(100));
        taskaktivetime.setTo(new Time(200));
        activeTimes.add(taskaktivetime);
        task.setActiveTimeFrames(activeTimes);
        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        List<ActiveTimes> parentactiveTimes = new ArrayList<>();
        parentGroup.setActiveTimeFrames(parentactiveTimes);
        //setParentGroup for task
        task.setGroup(parentGroup);
        //Act
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        //Arrange active times of parent group must be returned
        Assert.assertEquals(spy.getActiveTimesForTask(task), parentactiveTimes);
    }

    @Test
    public void testGetActiveTimesForTask_TaskParentAndGrandParentHaveActiveTimes() {
        //prepare Task
        Task task = new Task();
        task.setId("TEST");
        List<ActiveTimes> activeTimes = new ArrayList<ActiveTimes>();
        ActiveTimes taskaktivetime = new ActiveTimes();
        taskaktivetime.setFrom(new Time(100));
        taskaktivetime.setTo(new Time(200));
        activeTimes.add(taskaktivetime);
        task.setActiveTimeFrames(activeTimes);
        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        List<ActiveTimes> parentactiveTimes = new ArrayList<>();
        ActiveTimes parentactiveTime = new ActiveTimes();
        parentactiveTime.setFrom(new Time(400));
        parentactiveTime.setTo(new Time(600));
        parentactiveTimes.add(parentactiveTime);
        parentGroup.setActiveTimeFrames(parentactiveTimes);
        //prepare GrandParentGroup
        Group grandparentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        List<ActiveTimes> grandparentactiveTimes = new ArrayList<>();
        ActiveTimes grandparentactiveTime = new ActiveTimes();
        grandparentactiveTime.setFrom(new Time(800));
        grandparentactiveTime.setTo(new Time(1000));
        grandparentactiveTimes.add(grandparentactiveTime);
        grandparentGroup.setActiveTimeFrames(grandparentactiveTimes);
        //setParentGroup for task
        task.setGroup(parentGroup);
        parentGroup.setParentGroup(grandparentGroup);
        //Act
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        when(groupService.getGroupById(parentGroup.getParentGroup().getId())).thenReturn(grandparentGroup);
        //Arrange active times of grandparent group must be returned
        Assert.assertEquals(spy.getActiveTimesForTask(task), grandparentactiveTimes);

    }

    @Test
    public void testCheckIfTaskIsInActiveTime_ReturnsTrue() {
        Task task = new Task();
        ActiveTimes activeTimes = new ActiveTimes();
        activeTimes.setFrom(java.sql.Time.valueOf(LocalTime.now().minusSeconds(100)));
        activeTimes.setTo(java.sql.Time.valueOf(LocalTime.now().plusSeconds(100)));
        List<ActiveTimes> activeTimesList = new ArrayList<ActiveTimes>();
        activeTimesList.add(activeTimes);
        task.setActiveTimeFrames(activeTimesList);
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(any())).thenReturn(null);
        Assert.assertEquals(true, spy.checkIfTaskIsInActiveTime(task));
    }

    @Test
    public void testCheckIfTaskIsInActiveTime_ReturnsFalse() {
        Task task = new Task();
        ActiveTimes activeTimes = new ActiveTimes();
        activeTimes.setFrom(java.sql.Time.valueOf(LocalTime.now().plusSeconds(100)));
        activeTimes.setTo(java.sql.Time.valueOf(LocalTime.now().plusSeconds(100)));
        List<ActiveTimes> activeTimesList = new ArrayList<ActiveTimes>();
        activeTimesList.add(activeTimes);
        task.setActiveTimeFrames(activeTimesList);
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(any())).thenReturn(null);
        Assert.assertEquals(false, spy.checkIfTaskIsInActiveTime(task));
    }

    @Test
    public void testcheckIfTaskIsInWorkingDays_returnsTrue() {
        Task task = new Task();
        int[] workingdays = new int[]{1,1,1,1,1,1,1};
        task.setWorkingDays(workingdays);
        SchedulerService spy = spy(service);
        Assert.assertEquals(true,spy.checkIfTaskIsInWorkingDays(task));
    }
    @Test
    public void testcheckIfTaskIsInWorkingDays_returnsFalse() {
        Task task = new Task();
        int[] workingdays = new int[]{0,0,0,0,0,0,0};
        task.setWorkingDays(workingdays);
        SchedulerService spy = spy(service);
        Assert.assertEquals(false,spy.checkIfTaskIsInWorkingDays(task));
    }

}