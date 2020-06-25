package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.LockRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private LockRepository lockRepository;

    @MockBean
    private ICommunication sender;

    @MockBean
    private IGroupService groupService;

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    private ConvertUtils convertUtils;

    @Autowired
    private SchedulerService service;

    @Test
    public void testScheduleTask() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);

        Task t = createTaskTestObject(group,"TEST");

        SchedulerService spy = spy(service);
        when(taskService.getTaskById(t.getId())).thenReturn(Optional.of(t));
        when(lockRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.empty());
        when(taskRepository.findAllScheduledTasksSorted()).thenReturn(Collections.singletonList(t));
        doNothing().when(spy).sendTaskstoDispatcher(anyList());
        spy.scheduleTask(t.getId());

        verify(taskRepository).save(any());
        verify(taskService).calculatePriority(t);
        verify(taskRepository).findAllScheduledTasksSorted();
    }
    @Test
    public void testIfTaskNotFoundThenThrow() {
        Task t = createTaskTestObject(null,"TEST");

        when(taskService.getTaskById(t.getId())).thenReturn(Optional.empty());
        Exception e = assertThrows(RuntimeException.class, () -> service.scheduleTask(t.getId()));
        assertEquals("task could not be found in database with id: " + t.getId(), e.getMessage());
    }
    @Test
    public void testIfSchedulerIsLockedDontSend() {
        Group group = createGroupTestObject();
        group.setCurrentParallelismDegree(50);
        Task t = createTaskTestObject(group, "TEST");

        when(taskService.getTaskById(t.getId())).thenReturn(Optional.of(t));
        when(lockRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.of(new Lock()));
        when(taskRepository.findAllScheduledTasksSorted()).thenReturn(Collections.singletonList(t));
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
        verify(spy, never()).sendTaskstoDispatcher(any());
    }

    @Test
    public void testIsTaskLocked_NotLocked() {
        String taskId = UUID.randomUUID().toString();
        String lockId = LOCK_TASK_PREFIX + taskId;
        Lock lockObj = new Lock();
        lockObj.setId(lockId);
        when(lockRepository.findById(lockId)).thenReturn(Optional.of(lockObj));

        assertTrue(service.isTaskLocked(taskId));
    }

    @Test
    public void testIsTaskLocked_Locked() {
        String taskId = UUID.randomUUID().toString();
        String lockId = LOCK_TASK_PREFIX + taskId;
        when(lockRepository.findById(lockId)).thenReturn(Optional.empty());
        assertFalse(service.isTaskLocked(taskId));
    }

    @Test
    public void testIsGroupLocked_NotLocked() {
        String groupId = "GROUPID";
        String lockId = LOCK_GROUP_PREFIX + groupId;
        Lock lockObj = new Lock();
        lockObj.setId(lockId);
        when(lockRepository.findById(lockId)).thenReturn(Optional.of(lockObj));

        assertTrue(service.isGroupLocked(groupId));
    }

    @Test
    public void testIsGroupLocked_Locked() {
        String groupId = "GROUPID";
        String lockId = LOCK_GROUP_PREFIX + groupId;
        when(lockRepository.findById(lockId)).thenReturn(Optional.empty());

        assertFalse(service.isGroupLocked(groupId));
    }

    @Test
    public void testIsSchedulerLocked_NotLocked() {
        Lock lockObj = new Lock();
        lockObj.setId(LOCK_SCHEDULER_ALIAS);
        when(lockRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.of(lockObj));

        assertTrue(service.isSchedulerLocked());
    }

    @Test
    public void testIsSchedulerLocked_Locked() {
        when(lockRepository.findById(LOCK_SCHEDULER_ALIAS)).thenReturn(Optional.empty());

        assertFalse(service.isSchedulerLocked());
    }

    @Test
    public void testSendTasksToDispatcher() {
        //Arrange
        List<Task> tasks = new ArrayList<Task>();
        Group group = createGroupTestObject();
        Task task = createTaskTestObject(group, "TEST");
        tasks.add(task);

        SchedulerService spy = spy(service);
        Group groupIncremented = createGroupTestObject();
        groupIncremented.setCurrentParallelismDegree(group.getCurrentParallelismDegree()+1);
        when(groupRepository.incrementCurrentParallelismDegree(group.getId())).thenReturn(groupIncremented);
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task));

        // mock everything related to isPaused
        when(lockRepository.findById(LOCK_TASK_PREFIX + task.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(task.getId())).thenReturn(new ArrayList<>());
        doNothing().when(taskRepository).deleteById(task.getId());

        //Act
        spy.sendTaskstoDispatcher(tasks);

        //Assert
        assertEquals(1, groupIncremented.getCurrentParallelismDegree().intValue());
        verify(sender).sendTaskToDispatcher(task.getId());
        verify(taskRepository).deleteById(task.getId());
        verify(lockRepository,times(1)).findById(any());
        verify(spy).getLimitFromGroup(any(),any());
    }


    @Test
    public void testSendTasksToDispatcher_3TasksPresentinDatabase_IsOnlyAllowedToSend1() {

        Group group = createGroupTestObject();
        ArrayList<String> groupList = new ArrayList<>();
        groupList.add("456");
        Task task1 = createTaskTestObject(group, "5");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);
        tasks.add(task1);
        tasks.add(task1);
        SchedulerService spy = spy(service);
        Group groupIncremented = createGroupTestObject();
        groupIncremented.setCurrentParallelismDegree(task1.getGroup().getCurrentParallelismDegree()+1);
        when(groupRepository.incrementCurrentParallelismDegree(group.getId())).thenReturn(groupIncremented);
        when(groupService.getGroupById(group.getId())).thenReturn(group);
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task1));
        when(lockRepository.findById(LOCK_GROUP_PREFIX+"456")).thenReturn(Optional.empty());
        doNothing().when(taskRepository).deleteById(task1.getId());

        // mock everything related to isPaused
        when(lockRepository.findById(LOCK_TASK_PREFIX + task1.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(any())).thenReturn(groupList);

        spy.sendTaskstoDispatcher(tasks);

        assertEquals(1, task1.getGroup().getCurrentParallelismDegree().intValue());
        verify(sender,times(1)).sendTaskToDispatcher(task1.getId());
    }






    private Task createTaskTestObject(Group group, String id) {
        Task task = new Task();
        task.setId(id);
        task.setGroup(group);
        task.setActiveTimeFrames(null);
        return task;
    }

    @Test
    public void sendTasksToDispatcher_taskPaused() {
        Task task = new Task();
        task.setId("123");

        Group group = createGroupTestObject();
        Task task1 = createTaskTestObject(group,"TEST");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);

        Lock taskLock = new Lock();
        taskLock.setId(LOCK_TASK_PREFIX + task1.getId());

        SchedulerService spy = spy(service);
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task));

        // mock everything related to isPaused
        when(lockRepository.findById(taskLock.getId())).thenReturn(Optional.of(taskLock));
        when(taskService.getRecursiveGroupsOfTask(task1.getId())).thenReturn(new ArrayList<>());

        doNothing().when(taskRepository).deleteById(task1.getId());

        spy.sendTaskstoDispatcher(tasks);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertEquals(0, group.getCurrentParallelismDegree().intValue());
        verify(lockRepository, never()).save(any());
        verify(sender, never()).sendTaskToDispatcher(task1.getId());
        verify(taskRepository, never()).deleteById(task1.getId());
    }

    @Test
    public void sendTasksToDispatcher_groupPaused() {

        Group group = createGroupTestObject();
        group.setId("testGroup");


        Task task1 = createTaskTestObject(group,"5");
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task1);

        Lock groupLock = new Lock();
        groupLock.setId(LOCK_GROUP_PREFIX + "testGroup");

        SchedulerService spy = spy(service);

        // mock everything related to isPaused
        when(lockRepository.findById(LOCK_TASK_PREFIX + task1.getId())).thenReturn(Optional.empty());
        when(taskService.getRecursiveGroupsOfTask(task1.getId())).thenReturn(new ArrayList<>(Collections.singleton("testGroup")));
        when(lockRepository.findById(groupLock.getId())).thenReturn(Optional.of(groupLock));
        when(taskService.getTaskById(any())).thenReturn(Optional.of(task1));

        doNothing().when(taskRepository).deleteById(task1.getId());

        spy.sendTaskstoDispatcher(tasks);

        // assert, that task was not sent to dispatcher, not deleted from DB and capacity unchanged
        assertEquals(0, group.getCurrentParallelismDegree().intValue());
        verify(lockRepository, never()).save(any());
        verify(sender, never()).sendTaskToDispatcher(task1.getId());
        verify(taskRepository, never()).deleteById(task1.getId());
    }

    @Test
    public void testGetLimitFromGroup() {

        Group group1 = createGroupTestObject();
        group1.setParallelismDegree(15);
        group1.setId("1");

        Group group2 = createGroupTestObject();
        group2.setParallelismDegree(10);
        group2.setId("2");

        Group group3 = createGroupTestObject();
        group3.setParallelismDegree(5);
        group3.setId("3");

        group1.setParentGroup(group2);
        group2.setParentGroup(group3);

        List<String> groupsOfTask = new ArrayList<>();
        groupsOfTask.add(group1.getId());
        groupsOfTask.add(group2.getId());
        groupsOfTask.add(group3.getId());

        when(groupService.getGroupById("1")).thenReturn(group1);
        when(groupService.getGroupById("2")).thenReturn(group2);
        when(groupService.getGroupById("3")).thenReturn(group3);

        SchedulerService spy = spy(service);
        int limit = spy.getLimitFromGroup(groupsOfTask, group1.getId());
        assertEquals(limit,5);
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
        Assert.assertEquals(res , parentactiveTimes);
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
    @Test
    public void testgetActualWorkingDaysForTask_TaskHasNullWorkingDays_AndParentHasWorkingDays(){
        Task task = new Task();
        task.setId("TEST");
        int[] workingDays = null;
        task.setWorkingDays(workingDays);

        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = new int[]{0,0,0,0,0,1,0};
        parentGroup.setWorkingDays(parentworkingdays);
        task.setGroup(parentGroup);

        SchedulerService spy = spy(service);
        when(groupService.getGroupById(task.getGroup().getId())).thenReturn(parentGroup);
        Assert.assertEquals(spy.getActualWorkingDaysForTask(task), parentworkingdays);
    }

    @Test
    public void testgetActualWorkingDaysForTask_NoWorkingDaysPresent_GivesAll111111Array(){
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
        Assert.assertArrayEquals(spy.getActualWorkingDaysForTask(task), new int[]{1,1,1,1,1,1,1});
    }

    @Test(expected = RuntimeException.class)
    public void testgetActualWorkingDaysForTask_TaskHasnoGroupAndNoWorkingDaysThrowsException(){
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
    public void testgetActualWorkingDaysForTask_Task_ParentAndGrandparentHaveWorkingDays_AndGivesUsWorkingDaysFromTask(){
        //prepare Task
        Task task = new Task();
        task.setId("TEST");
        int[] workingdays = new int[]{0,0,0,0,0,0,0};
        task.setWorkingDays(workingdays);

        //prepare ParentGroup
        Group parentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] parentworkingdays = new int[]{0,0,0,0,0,1,0};
        parentGroup.setWorkingDays(parentworkingdays);

        //prepare GrandParentGroup
        Group grandparentGroup = new Group();
        parentGroup.setId("TESTPARENTGROUP");
        int[] grandparentworkingdays = new int[]{0,0,0,0,0,1,0};
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
    public void testsequentialCheckReturnsFalse(){
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
    public void testsequentialCheckReturnsTrue(){
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
}