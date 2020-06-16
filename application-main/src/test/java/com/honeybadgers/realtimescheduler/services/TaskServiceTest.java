package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.realtimescheduler.model.GroupAncestorModel;
import com.honeybadgers.realtimescheduler.repository.GroupAncestorRepository;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskService.class)
public class TaskServiceTest {

    @MockBean
    private TaskPostgresRepository taskPostgresRepository;

    @MockBean
    GroupAncestorRepository groupAncestorRepository;

    @Autowired
    private TaskService service;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-bonus-base-prio-dependant}")
    boolean deadlineBaseDependant;

    @Test
    public void testGetAllTasks() {
        TaskService spy = spy(service);
        spy.getAllTasks();
        verify(taskPostgresRepository).findAll();
    }

    @Test
    public void testUploadTask() {
        TaskService spy = spy(service);
        spy.uploadTask(any());
        verify(taskPostgresRepository).save(Mockito.any());
    }

    @Test
    public void testDeleteTaskWithCorrectId() {
        String id = "123";
        TaskService spy = spy(service);
        spy.deleteTask(id);
        verify(taskPostgresRepository).deleteById(id);
    }

    @Test
    public void testGetTaskById() {
        TaskService spy = spy(service);
        spy.getTaskById("123");
        verify(taskPostgresRepository).findById("123");
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

        long res = service.calculatePriority(prio);
        long res2 = service.calculatePriority(prio2);
        long res3 = service.calculatePriority(prio3);
        long res4 = service.calculatePriority(deadline0);
        long res5 = service.calculatePriority(deadline1);
        long res6 = service.calculatePriority(deadline2);
        long res7 = service.calculatePriority(deadline3);
        long res8 = service.calculatePriority(deadline4);
        System.out.println("prio: " + res);
        System.out.println("prio2: " + res2);
        System.out.println("prio3: " + res3);
        System.out.println("deadline0: " + res4);
        System.out.println("deadline1: " + res5);
        System.out.println("deadline2: " + res6);
        System.out.println("deadline3: " + res7);
        System.out.println("deadline4: " + res8);

        Assert.assertTrue(true);
    }

    @Test
    public void testGetAllTasks2() {
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 1; i < 4; i++) {
            Task t = new Task();
            t.setId(String.valueOf(i));
            tasks.add(t);
        }
        Mockito.when(taskPostgresRepository.findAll()).thenReturn(tasks);
        List<Task> returnedTasks = service.getAllTasks();
        Assert.assertEquals(tasks, returnedTasks);
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
        ancestorModel.setAncestors(new String[] {parent.getId()});

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
        assertEquals("CRITICAL: found task with taskId " + task.getId() + " which has no group -> THIS SHOULD NOT HAVE HAPPENED (DB enforces this)!", e.getMessage());
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
        ancestorModel.setAncestors(new String[] {parent.getId()});

        when(service.getTaskById("test")).thenReturn(Optional.of(task));
        when(groupAncestorRepository.getAllAncestorIdsFromGroup("testGroup")).thenReturn(Optional.of(ancestorModel));

        Exception e = assertThrows(IllegalStateException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("AncestorModel received from repository contains null values!", e.getMessage());
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
        ancestorModel.setAncestors(new String[] {parent.getId(), null});

        when(service.getTaskById("test")).thenReturn(Optional.of(task));
        when(groupAncestorRepository.getAllAncestorIdsFromGroup("testGroup")).thenReturn(Optional.of(ancestorModel));

        Exception e = assertThrows(IllegalStateException.class, () -> service.getRecursiveGroupsOfTask(task.getId()));

        assertNotNull(e);
        assertEquals("Ancestor list contains null values!", e.getMessage());
    }
  
}

