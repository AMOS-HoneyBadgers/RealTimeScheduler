package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.jpa.*;
import com.honeybadgers.postgre.repository.GroupAncestorRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskService.class)
@PropertySource("classpath:application-test.properties")
public class TaskServiceTest {

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    GroupAncestorRepository groupAncestorRepository;

    @Autowired
    private TaskService service;

    @Test
    public void testFinishTask() {
        TaskService spy = spy(service);
        Task t = new Task();
        spy.finishTask(t);
        t.setStatus(TaskStatusEnum.Finished);
        verify(taskRepository).save(t);
    }

    @Test
    public void testGetTaskById() {
        TaskService spy = spy(service);
        spy.getTaskById("123");
        verify(taskRepository).findById("123");
    }

    @Test
    public void testCalculatePriority() {
        Task newTask = new Task();
        newTask.setId("TEST");
        newTask.setPriority(20);

        long prio = service.calculatePriority(newTask);

        // If const = 1000 and prio-modifier = 1
        Assert.assertEquals(980, prio);

        // set deadline and check final priority
        newTask.setDeadline(new Timestamp(System.currentTimeMillis() + 100000));
        prio = service.calculatePriority(newTask);
        Assert.assertTrue(prio > 980);

        // create new Task with lower deadline and check final priority
        Task newTaskHigherPrio = new Task();
        newTaskHigherPrio.setId("TEST2");
        newTaskHigherPrio.setPriority(20);
        newTaskHigherPrio.setDeadline(new Timestamp(System.currentTimeMillis() + 50000));
        long higherPrio = service.calculatePriority(newTaskHigherPrio);
        Assert.assertTrue(higherPrio > prio);
    }

    @Test
    public void testInspectPrioResults() {
        Task prio = new Task();
        prio.setId("TEST");
        prio.setPriority(1);

        Task prio2 = new Task();
        prio2.setId("TEST");
        prio2.setPriority(100);

        Task prio3 = new Task();
        prio3.setId("TEST");
        prio3.setPriority(1000);

        Group groupPrio4 = new Group();
        groupPrio4.setPriority(100);
        Task prio4 = new Task();
        prio4.setId("TEST");
        prio4.setGroup(groupPrio4);

        Task deadline0 = new Task();
        deadline0.setId("TEST");
        deadline0.setPriority(100);
        // 1 minute
        deadline0.setDeadline(new Timestamp(System.currentTimeMillis() + 60000));

        Task deadline1 = new Task();
        deadline1.setId("TEST");
        deadline1.setPriority(100);
        // 10 minutes
        deadline1.setDeadline(new Timestamp(System.currentTimeMillis() + 600000));

        Task deadline2 = new Task();
        deadline2.setId("TEST");
        deadline2.setPriority(100);
        // 1 hour
        deadline2.setDeadline(new Timestamp(System.currentTimeMillis() + 3600000));

        Task deadline3 = new Task();
        deadline3.setId("TEST");
        deadline3.setPriority(100);
        // 1 day
        deadline3.setDeadline(new Timestamp(System.currentTimeMillis() + 86400000));

        Task deadline4 = new Task();
        deadline4.setId("TEST");
        deadline4.setPriority(100);
        // 10 days
        deadline4.setDeadline(new Timestamp(System.currentTimeMillis() + 864000000));

        // Following calculations with retries and realtime tasks are with priority 100

        Task retries0 = new Task();
        retries0.setId("TEST");
        retries0.setPriority(100);
        // 1 retry
        retries0.setRetries(1);

        Task retries1 = new Task();
        retries1.setId("TEST");
        retries1.setPriority(100);
        // 3 retries
        retries1.setRetries(3);

        Task retries2 = new Task();
        retries2.setId("TEST");
        retries2.setPriority(100);
        // 5 retries
        retries2.setRetries(5);

        Task batch0 = new Task();
        batch0.setId("TEST");
        batch0.setPriority(100);
        // set as realtime
        batch0.setTypeFlagEnum(TypeFlagEnum.Realtime);

        Task noValues = new Task();
        noValues.setId("TEST");

        long res = service.calculatePriority(prio);
        long res2 = service.calculatePriority(prio2);
        long res3 = service.calculatePriority(prio3);
        long res4 = service.calculatePriority(deadline0);
        long res5 = service.calculatePriority(deadline1);
        long res6 = service.calculatePriority(deadline2);
        long res7 = service.calculatePriority(deadline3);
        long res8 = service.calculatePriority(deadline4);
        long res9 = service.calculatePriority(retries0);
        long res10 = service.calculatePriority(retries1);
        long res11 = service.calculatePriority(retries2);
        long res12 = service.calculatePriority(noValues);
        long res13 = service.calculatePriority(batch0);
        long res14 = service.calculatePriority(prio4);
        System.out.println("prio: " + res);
        System.out.println("prio2: " + res2);
        System.out.println("prio3: " + res3);
        System.out.println("prio4: " + res14);
        System.out.println("deadline0: " + res4);
        System.out.println("deadline1: " + res5);
        System.out.println("deadline2: " + res6);
        System.out.println("deadline3: " + res7);
        System.out.println("deadline4: " + res8);
        System.out.println("retries0: " + res9);
        System.out.println("retries1: " + res10);
        System.out.println("retries2: " + res11);
        System.out.println("batch0: " + res13);
        System.out.println("noValues: " + res12);


        Assert.assertTrue(res > res2);
        Assert.assertTrue(res2 > res3);
        Assert.assertTrue(res4 > res5);
        Assert.assertTrue(res5 > res6);
        Assert.assertTrue(res7 > res8);
        Assert.assertTrue(res9 > res10);
        Assert.assertTrue(res10 > res11);
        Assert.assertEquals(res14, res2);

    }

    @Test
    public void testGetRecursiveGroupsOfTask() {

        Group parent = new Group();
        parent.setId("testGroupParent");

        Group group = new Group();
        group.setId("testGroup");
        group.setParentGroup(parent);

        Task task = new Task();
        task.setId("test");
        task.setGroup(group);

        GroupAncestorModel ancestorModel = new GroupAncestorModel();
        ancestorModel.setId(group.getId());
        ancestorModel.setAncestors(new String[]{parent.getId()});

        when(service.getTaskById("test")).thenReturn(Optional.of(task));
        when(groupAncestorRepository.getAllAncestorIdsFromGroup("testGroup")).thenReturn(Optional.of(ancestorModel));

        List<String> groups = service.getRecursiveGroupsOfTask(task.getId());

        assertNotNull(groups);
        assertEquals(2, groups.size());
        assertArrayEquals(new String[]{group.getId(), parent.getId()}, groups.toArray());
    }

    @Test
    public void testGetRecursiveGroupsOfTask_nullInput() {

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.getRecursiveGroupsOfTask(null));

        assertNotNull(e);
        assertEquals("taskId must not be null!", e.getMessage());
    }

    @Test
    public void testGetRecursiveGroupsOfTask_notFound() {

        Group parent = new Group();
        parent.setId("testGroupParent");

        Group group = new Group();
        group.setId("testGroup");
        group.setParentGroup(parent);

        Task task = new Task();
        task.setId("test");
        task.setGroup(group);

        when(service.getTaskById("test")).thenReturn(Optional.empty());

        Exception e = assertThrows(NoSuchElementException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("Task with taskId " + task.getId() + " not found!", e.getMessage());
    }

    @Test
    public void testGetRecursiveGroupsOfTask_noGroupERROR() {

        Task task = new Task();
        task.setId("test");

        when(service.getTaskById("test")).thenReturn(Optional.of(task));

        Exception e = assertThrows(IllegalStateException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("CRITICAL: found task with taskId test which has no group!", e.getMessage());
    }

    @Test
    public void testGetRecursiveGroupsOfTask_invalidAncestorModel() {

        Group parent = new Group();
        parent.setId("testGroupParent");

        Group group = new Group();
        group.setId("testGroup");
        group.setParentGroup(parent);

        Task task = new Task();
        task.setId("test");
        task.setGroup(group);

        GroupAncestorModel ancestorModel = new GroupAncestorModel();
        ancestorModel.setAncestors(new String[]{parent.getId()});

        when(service.getTaskById("test")).thenReturn(Optional.of(task));
        when(groupAncestorRepository.getAllAncestorIdsFromGroup("testGroup")).thenReturn(Optional.of(ancestorModel));

        Exception e = assertThrows(IllegalStateException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("AncestorModel received from repository contains null values for taskId: test", e.getMessage());
    }

    @Test
    public void testGetRecursiveGroupsOfTask_invalidReturn() {

        Group parent = new Group();
        parent.setId("testGroupParent");

        Group group = new Group();
        group.setId("testGroup");
        group.setParentGroup(parent);

        Task task = new Task();
        task.setId("test");
        task.setGroup(group);

        GroupAncestorModel ancestorModel = new GroupAncestorModel();
        ancestorModel.setId(group.getId());
        ancestorModel.setAncestors(new String[]{parent.getId(), null});

        when(service.getTaskById("test")).thenReturn(Optional.of(task));
        when(groupAncestorRepository.getAllAncestorIdsFromGroup("testGroup")).thenReturn(Optional.of(ancestorModel));

        Exception e = assertThrows(IllegalStateException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("Ancestor list contains null values for taskId: test", e.getMessage());
    }

    @Test
    public void testUpdateTaskStatus_NoHistoryElement() {
        Task taskNoHistory = new Task();
        taskNoHistory.setHistory(null);

        service.updateTaskStatus(taskNoHistory, TaskStatusEnum.Waiting);

        assertNotNull(taskNoHistory.getHistory());
        assertEquals(1, taskNoHistory.getHistory().size());
        assertEquals(TaskStatusEnum.Waiting, taskNoHistory.getStatus());
    }

    @Test
    public void testUpdateTaskStatus() {
        Task task = new Task();
        List<History> history = new ArrayList<>();

        History hist = new History();
        hist.setStatus(TaskStatusEnum.Waiting.toString());
        hist.setTimestamp(Timestamp.from(Instant.now()));
        history.add(hist);
        task.setHistory(history);

        service.updateTaskStatus(task, TaskStatusEnum.Scheduled);

        assertEquals(2, task.getHistory().size());
        assertEquals(TaskStatusEnum.Scheduled, task.getStatus());

    }
}

