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
        // don't know why this isn't working yet
        deadlineBaseDependant = true;

        long prio = service.calculatePriority(newTask);
        // has same final priority because deadline is not set
        Assert.assertEquals(20, prio);

        // set deadline and check final priority
        newTask.setDeadline(new Timestamp(System.currentTimeMillis() + 100000));
        prio = service.calculatePriority(newTask);
        Assert.assertTrue(prio > 20);

        // create new Task with lower deadline and check final priority
        Task newTaskHigherPrio = new Task();
        newTaskHigherPrio.setId("TEST2");
        newTaskHigherPrio.setPriority(20);
        newTaskHigherPrio.setDeadline(new Timestamp(System.currentTimeMillis() + 50000));
        long higherPrio = service.calculatePriority(newTaskHigherPrio);
        Assert.assertTrue(higherPrio > prio);


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

