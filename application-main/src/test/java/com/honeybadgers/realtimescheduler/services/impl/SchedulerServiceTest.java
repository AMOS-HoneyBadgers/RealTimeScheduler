package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.exceptions.LockException;
import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.PausedRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.hibernate.TransactionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static com.honeybadgers.models.model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SchedulerService.class)
public class SchedulerServiceTest {

    @MockBean
    private ITaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private PausedRepository pausedRepository;

    @MockBean
    private ICommunication sender;

    @MockBean
    private IGroupService groupService;

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    private ConvertUtils convertUtils;

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    SchedulerService service;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Test
    public void testScheduleTaskWrapper() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);
        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.empty());
        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));
        // unfortunately not possible to mock checkTaskForDispatchingAndUpdate due to calling method on _self proxy
        // (mocking self results into mocking service which makes this tests useless)

        spy.scheduleTaskWrapper("as");

        verify(taskRepository, times(2)).save(any());// once in scheduleTask and once in checkTaskForDispatchingAndUpdate
        verify(taskService).calculatePriority(t);
        verify(taskRepository, times(1)).getTasksToBeDispatched(anyInt());
    }

    @Test
    public void testScheduleTaskWrapper_schedulerTrigger() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");
        Task t2 = createTaskTestObject(group, "TEST2");
        SchedulerService spy = spy(service);
        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.empty());
        when(taskRepository.findAllScheduledTasksSorted()).thenReturn(Collections.singletonList(t2));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Arrays.asList(t, t2));
        // unfortunately not possible to mock checkTaskForDispatchingAndUpdate due to calling method on _self proxy
        // (mocking self results into mocking service which makes this tests useless)

        spy.scheduleTaskWrapper(scheduler_trigger);

        verify(taskRepository, never()).findAllWaitingTasks();
        verify(taskRepository, times(1)).findAllScheduledTasksSorted();
        verify(taskRepository, times(1)).getTasksToBeDispatched(anyInt());
        verify(taskRepository, times(3)).save(any());// once in scheduleTask and twice (once for each task) in checkTaskForDispatchingAndUpdate
        verify(taskService).calculatePriority(t2);
    }

    @Test
    public void testScheduleTaskWrapper_sendToDispatcher_LockException() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);

        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));

        when(spy.isTaskPaused(t.getId())).thenThrow(new CannotAcquireLockException(""));

        spy.scheduleTaskWrapper("as");
    }

    @Test
    public void testScheduleTaskWrapper_sendToDispatcher_TransactionException() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);

        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));

        when(spy.isTaskPaused(t.getId())).thenThrow(new TransactionException(""));

        spy.scheduleTaskWrapper("as");
    }

    @Test
    public void testScheduleTaskWrapper_scheduleTask_LockException() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);

        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));

        when(taskService.calculatePriority(any())).thenThrow(new CannotAcquireLockException(""));

        spy.scheduleTaskWrapper("as");

        // still try to dispatch even if prio calc fails
        verify(taskRepository, times(1)).getTasksToBeDispatched(anyInt());
    }

    @Test
    public void testScheduleTaskWrapper_scheduleTask_TransactionException() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);

        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));

        when(taskService.calculatePriority(any())).thenThrow(new TransactionException(""));

        spy.scheduleTaskWrapper("as");

        verify(taskRepository).getTasksToBeDispatched(anyInt());
    }


    @Test
    public void testScheduleTaskWrapper_3TasksPresentInDatabase_IsOnlyAllowedToSend1() {

        Group group = createGroupTestObject();
        Group groupAncestor = createGroupTestObject();
        groupAncestor.setId("789");
        group.setParentGroup(groupAncestor);
        ArrayList<String> groupList = new ArrayList<>();
        groupList.add("456");
        groupList.add("789");
        Task task1 = createTaskTestObject(group, "5");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);
        tasks.add(task1);
        tasks.add(task1);
        SchedulerService spy = spy(service);
        when(restTemplate.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(new LockResponse("Name", "Value", null, false), HttpStatus.OK));
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(taskRepository.findAllWaitingTasks()).thenReturn(tasks);
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(tasks);
        when(groupRepository.incrementCurrentParallelismDegree(anyString())).then(invocationOnMock -> {
            group.setCurrentParallelismDegree(group.getCurrentParallelismDegree()+1);
            return group;
        }).then(invocationOnMock -> {
            groupAncestor.setCurrentParallelismDegree(groupAncestor.getCurrentParallelismDegree()+1);
            return groupAncestor;
        });
        when(groupService.getGroupById(group.getId())).thenReturn(group);
        when(groupService.getGroupById(groupAncestor.getId())).thenReturn(groupAncestor);
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task1));
        when(pausedRepository.findById(PAUSED_GROUP_PREFIX +"456")).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        // mock everything related to isPaused
        when(pausedRepository.findById(PAUSED_TASK_PREFIX + task1.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(any())).thenReturn(groupList);

        doNothing().when(spy).scheduleTask(any());

        spy.scheduleTaskWrapper("as");

        assertEquals(1, task1.getGroup().getCurrentParallelismDegree().intValue());
        assertEquals(1, groupAncestor.getCurrentParallelismDegree().intValue());
        verify(sender,times(1)).sendTaskToDispatcher(task1.getId());
        verify(groupRepository, times(2)).incrementCurrentParallelismDegree(anyString());
    }

    @Test
    public void testIfSchedulerIsLockedDontSend() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);
        Task t = createTaskTestObject(group, "TEST");

        when(taskRepository.findAllWaitingTasks()).thenReturn(Collections.singletonList(t));
        when(taskRepository.getTasksToBeDispatched(anyInt())).thenReturn(Collections.singletonList(t));
        when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.of(new Paused()));
        //when(taskService.getTaskById(t.getId())).thenReturn(Optional.of(t));
        SchedulerService spy = spy(service);
        spy.scheduleTaskWrapper("123");
        verify(spy, never()).checkTaskForDispatchingAndUpdate(any());
    }

    @Test
    public void testIsTaskLocked_NotLocked() {
        String taskId = UUID.randomUUID().toString();
        String pausedId = PAUSED_TASK_PREFIX + taskId;
        Paused paused = new Paused();
        paused.setId(pausedId);
        when(pausedRepository.findById(pausedId)).thenReturn(Optional.of(paused));

        assertTrue(service.isTaskPaused(taskId));
    }

    @Test
    public void testIsTaskLocked_Locked() {
        String taskId = UUID.randomUUID().toString();
        String pausedId = PAUSED_TASK_PREFIX + taskId;
        when(pausedRepository.findById(pausedId)).thenReturn(Optional.empty());
        assertFalse(service.isTaskPaused(taskId));
    }

    @Test
    public void testIsGroupLocked_NotLocked() {
        String groupId = "GROUPID";
        String pausedId = PAUSED_GROUP_PREFIX + groupId;
        Paused paused = new Paused();
        paused.setId(pausedId);
        when(pausedRepository.findById(pausedId)).thenReturn(Optional.of(paused));

        assertTrue(service.isGroupPaused(groupId));
    }

    @Test
    public void testIsGroupLocked_Locked() {
        String groupId = "GROUPID";
        String pausedId = PAUSED_GROUP_PREFIX + groupId;
        when(pausedRepository.findById(pausedId)).thenReturn(Optional.empty());

        assertFalse(service.isGroupPaused(groupId));
    }

    @Test
    public void testIsSchedulerLocked_NotLocked() {
        Paused paused = new Paused();
        paused.setId(PAUSED_SCHEDULER_ALIAS);
        when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.of(paused));

        assertTrue(service.isSchedulerPaused());
    }

    @Test
    public void testIsSchedulerLocked_Locked() {
        when(pausedRepository.findById(PAUSED_SCHEDULER_ALIAS)).thenReturn(Optional.empty());

        assertFalse(service.isSchedulerPaused());
    }

    @Test
    public void testCheckTaskForDispatchingAndUpdate() {
        //Arrange
        Group group = createGroupTestObject();
        Task task = createTaskTestObject(group, "TEST");

        SchedulerService spy = spy(service);

        when(taskService.getRecursiveGroupsOfTask(task.getId())).thenReturn(new ArrayList<>());
        when(groupService.getGroupById(anyString())).thenReturn(group);
        when(groupRepository.incrementCurrentParallelismDegree(group.getId())).then(invocationOnMock -> {
            group.setCurrentParallelismDegree(group.getCurrentParallelismDegree()+1);
            return group;
        });

        // mock everything related to isPaused
        when(pausedRepository.findById(PAUSED_TASK_PREFIX + task.getId())).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        //Act
        boolean ret = spy.checkTaskForDispatchingAndUpdate(task);

        //Assert
        assertTrue(ret);
        assertEquals(0, group.getCurrentParallelismDegree().intValue());
        verify(groupRepository, never()).incrementCurrentParallelismDegree(anyString());
        verify(taskRepository).save(any(Task.class));
        verify(pausedRepository,times(1)).findById(any());
        verify(spy).checkParallelismDegreeSurpassed(anyList(),anyString());
    }

    @Test
    public void testCheckParallelismDegreeSurpassed(){
        Group group = createGroupTestObject();
        Group groupAncestor = createGroupTestObject();
        group.setParentGroup(groupAncestor);

        when(groupService.getGroupById(group.getId())).thenReturn(group);
        when(groupService.getGroupById(groupAncestor.getId())).thenReturn(groupAncestor);

        List<String> groups = Arrays.asList(group.getId(), groupAncestor.getId());

        SchedulerService spy = spy(service);
        boolean ret = spy.checkParallelismDegreeSurpassed(groups,"testTask");

        assertFalse(ret);
    }

    @Test
    public void testCheckParallelismDegreeSurpassed_ancestorSurpassed(){
        Group group = createGroupTestObject();
        Group groupAncestor = createGroupTestObject();
        groupAncestor.setCurrentParallelismDegree(1);
        group.setParentGroup(groupAncestor);

        when(groupService.getGroupById(group.getId())).thenReturn(group);
        when(groupService.getGroupById(groupAncestor.getId())).thenReturn(groupAncestor);

        List<String> groups = Arrays.asList(group.getId(), groupAncestor.getId());

        SchedulerService spy = spy(service);
        boolean ret = spy.checkParallelismDegreeSurpassed(groups, "testTask");

        assertTrue(ret);
    }

    private Task createTaskTestObject(Group group, String id) {
        Task task = new Task();
        task.setId(id);
        task.setGroup(group);
        task.setActiveTimeFrames(null);
        return task;
    }

    @Test
    public void testCheckTaskForDispatchingAndUpdate_taskPaused() {
        Group group = createGroupTestObject();
        Task task = createTaskTestObject(group,"TEST");

        Paused taskPaused = new Paused();
        taskPaused.setId(PAUSED_TASK_PREFIX + task.getId());

        SchedulerService spy = spy(service);

        // mock everything related to isPaused
        when(pausedRepository.findById(taskPaused.getId())).thenReturn(Optional.of(taskPaused));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        boolean ret = spy.checkTaskForDispatchingAndUpdate(task);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertFalse(ret);
        assertEquals(0, group.getCurrentParallelismDegree().intValue());
        verify(pausedRepository, never()).save(any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testCheckTaskForDispatchingAndUpdate_groupPaused() {

        Group group = createGroupTestObject();
        group.setId("testGroup");

        Task task = createTaskTestObject(group, "5");

        Paused groupPaused = new Paused();
        groupPaused.setId(PAUSED_GROUP_PREFIX + "testGroup");

        SchedulerService spy = spy(service);

        when(taskService.getRecursiveGroupsOfTask(task.getId())).thenReturn(new ArrayList<>(Collections.singleton("testGroup")));

        // mock everything related to isPaused
        when(pausedRepository.findById(PAUSED_TASK_PREFIX + task.getId())).thenReturn(Optional.empty());
        when(pausedRepository.findById(groupPaused.getId())).thenReturn(Optional.of(groupPaused));


        boolean ret = spy.checkTaskForDispatchingAndUpdate(task);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertFalse(ret);
        assertEquals(0, group.getCurrentParallelismDegree().intValue());
        verify(pausedRepository, never()).save(any());
        verify(taskRepository, never()).save(any(Task.class));
    }


    private Group createGroupTestObject() {
        Group group = new Group();
        group.setId("456");
        group.setParallelismDegree(1);
        group.setParentGroup(null);
        group.setWorkingDays(new int[]{1,1,1,1,1,1,1});
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
    public void testGetActiveTimesForTask_TaskHasNoActiveTimesAndParentHaveActiveTimes() {
        //prepare Task
        Task task = new Task();
        task.setId("TEST");
        task.setActiveTimeFrames(null);

        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        List<ActiveTimes> parentactiveTimes = new ArrayList<>();
        ActiveTimes parenttaskaktivetime = new ActiveTimes();
        parenttaskaktivetime.setFrom(new Time(800));
        parenttaskaktivetime.setTo(new Time(1200));
        parentactiveTimes.add(parenttaskaktivetime);
        parentGroup.setActiveTimeFrames(parentactiveTimes);

        //setParentGroup for task
        task.setGroup(parentGroup);

        //Act
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);

        //Arrange
        List<ActiveTimes> res = spy.getActiveTimesForTask(task);
        Assert.assertEquals(res, parentactiveTimes);
    }

    @Test
    public void testGetActiveTimesForTask_TaskAndParentAndGrandparentHasActiveTimes_receivesTaskActiveTimes() {
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

        //Arrange
        List<ActiveTimes> res = spy.getActiveTimesForTask(task);
        Assert.assertEquals(res, activeTimes);
    }

    @Test
    public void testGetActiveTimesForTask_TaskAndParentAndGrandparentDontHaveActiveTimes_ExpectedEmptyList() {
        //prepare Task
        Task task = new Task();
        task.setId("TEST");

        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");

        //prepare GrandParentGroup
        Group grandparentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");

        //setParentGroup for task
        task.setGroup(parentGroup);
        parentGroup.setParentGroup(grandparentGroup);

        //Act
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        when(groupService.getGroupById(parentGroup.getParentGroup().getId())).thenReturn(grandparentGroup);

        //Arrange
        List<ActiveTimes> res = spy.getActiveTimesForTask(task);
        Assert.assertEquals(res.size(), new ArrayList<>().size());
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
        int[] workingdays = new int[]{1, 1, 1, 1, 1, 1, 1};
        task.setWorkingDays(workingdays);
        SchedulerService spy = spy(service);
        Assert.assertEquals(true, spy.checkIfTaskIsInWorkingDays(task));
    }

    @Test
    public void testcheckIfTaskIsInWorkingDays_returnsFalse() {
        Task task = new Task();
        int[] workingdays = new int[]{0, 0, 0, 0, 0, 0, 0};
        task.setWorkingDays(workingdays);
        SchedulerService spy = spy(service);
        Assert.assertEquals(false, spy.checkIfTaskIsInWorkingDays(task));
    }

    @Test
    public void testgetActualWorkingDaysForTask_TaskHasNullWorkingDays_AndParentHasWorkingDays() {
        Task task = new Task();
        task.setId("TEST");
        int[] workingDays = null;
        task.setWorkingDays(workingDays);

        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = new int[]{0, 0, 0, 0, 0, 1, 0};
        parentGroup.setWorkingDays(parentworkingdays);
        task.setGroup(parentGroup);

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        Assert.assertEquals(spy.getActualWorkingDaysForTask(task), parentworkingdays);
    }

    @Test
    public void testgetActualWorkingDaysForTask_NoWorkingDaysPresent_GivesAll111111Array() {
        Task task = new Task();
        task.setId("TEST");
        int[] workingDays = null;
        task.setWorkingDays(workingDays);

        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = null;
        parentGroup.setWorkingDays(parentworkingdays);

        //prepare GrandParentGroup
        Group grandparentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] grandparentworkingdays = null;
        grandparentGroup.setWorkingDays(grandparentworkingdays);

        //setParentGroup for task
        task.setGroup(parentGroup);
        parentGroup.setParentGroup(grandparentGroup);

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        Assert.assertArrayEquals(spy.getActualWorkingDaysForTask(task), new int[]{1, 1, 1, 1, 1, 1, 1});
    }

    @Test(expected = RuntimeException.class)
    public void testgetActualWorkingDaysForTask_TaskHasnoGroupAndNoWorkingDaysThrowsException() {
        Task task = new Task();
        task.setId("TEST");
        int[] workingDays = null;
        task.setWorkingDays(workingDays);

        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = null;
        parentGroup.setWorkingDays(parentworkingdays);

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
    }

    @Test
    public void testgetActualWorkingDaysForTask_Task_ParentAndGrandparentHaveWorkingDays_AndGivesUsWorkingDaysFromTask() {
        //prepare Task
        Task task = new Task();
        task.setId("TEST");
        int[] workingdays = new int[]{0, 0, 0, 0, 0, 0, 0};
        task.setWorkingDays(workingdays);

        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = new int[]{0, 0, 0, 0, 0, 1, 0};
        parentGroup.setWorkingDays(parentworkingdays);

        //prepare GrandParentGroup
        Group grandparentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] grandparentworkingdays = new int[]{0, 0, 0, 0, 0, 1, 0};
        grandparentGroup.setWorkingDays(grandparentworkingdays);

        //setParentGroup for task
        task.setGroup(parentGroup);
        parentGroup.setParentGroup(grandparentGroup);
        //Act
        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        when(groupService.getGroupById(parentGroup.getParentGroup().getId())).thenReturn(grandparentGroup);
        //Arrange active times of grandparent group must be returned
        Assert.assertEquals(spy.getActualWorkingDaysForTask(task), workingdays);
    }

    @Test
    public void testsequentialCheckReturnsFalse() {
        //Arrange
        Task task = new Task();
        task.setModeEnum(ModeEnum.Sequential);

        //the index number of the parentgroup starts at 0 and the indexnumber of the task must be one higher
        task.setIndexNumber(1);
        Group parentgroup = new Group();
        parentgroup.setLastIndexNumber(0);
        task.setGroup(parentgroup);

        //Act
        SchedulerService spy = spy(service);

        //Assert
        Assert.assertFalse(spy.sequentialHasToWait(task));
    }


    @Test
    public void testSequentialCheckReturnsTrue() {
        //Arrange
        Task task = new Task();
        task.setModeEnum(ModeEnum.Sequential);

        task.setIndexNumber(0);
        Group parentgroup = new Group();
        parentgroup.setLastIndexNumber(1);
        task.setGroup(parentgroup);

        //Act
        SchedulerService spy = spy(service);
        //Assert
        Assert.assertTrue(spy.sequentialHasToWait(task));
    }

    @Test()
    public void testRunWithBAD_REQUESTSchedulerIsStopped() {
        //Arrange
        LockResponse lockResponse = new LockResponse();
        lockResponse.setName("SCHEDULER");
        lockResponse.setValue("value");
        Thread t = new SchedulerService.LockRefresherThread(lockResponse, restTemplate);
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        //Act
        t.run();
        Assert.assertTrue(SchedulerService.stopSchedulerDueToLockAcquisitionException);
    }
    @Test(expected = LockException.class)
    public void testCheckIfAllowedToScheduleWithBAD_REQUESTThrowsLockException() {
        //Arrange
        when(restTemplate.postForEntity(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        //Act
        SchedulerService spy = spy(service);
        spy.checkIfAllowedtoSchedule();

    }

}