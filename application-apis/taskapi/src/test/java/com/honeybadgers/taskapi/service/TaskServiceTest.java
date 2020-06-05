package com.honeybadgers.taskapi.service;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.Task;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.repository.GroupRepository;
import com.honeybadgers.taskapi.repository.TaskRepository;
import com.honeybadgers.taskapi.service.impl.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskService.class)
public class TaskServiceTest {

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    GroupRepository groupRepository;
    @MockBean
    ICommunication communication;

    @Autowired
    ITaskService taskService;


    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    }

    @Test
    public void testCreateTask() throws JpaException, UnknownEnumException, CreationException {

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Task t = taskService.createTask(restModel);

        assertNotNull(t);
        assertThat(t.getId()).isEqualTo(restModel.getId().toString());
    }

    @Test
    public void testCreateTask_group404() {

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroupNotFound");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals("Group not found!", e.getMessage());
    }

    @Test
    public void testCreateTask_primaryKeyViolation() {

        DataIntegrityViolationException vio = new DataIntegrityViolationException("primary key violation");

        when(taskRepository.save(any(Task.class))).thenThrow(vio);

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals("Primary or unique constraint failed!", e.getMessage());
    }

    @Test
    public void testCreateTask_groupChildrenViolation() {

        Group child = new Group();
        child.setId("TestGroup");
        Group child2 = new Group();
        child2.setId("TestGroup2");
        when(groupRepository.findAllByParentGroupId("testGroup")).thenReturn(Arrays.asList(child, child2));

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Exception e = assertThrows(CreationException.class, () -> taskService.createTask(restModel));
        assertEquals("Group of task has other groups as children: TestGroup, TestGroup2 -> aborting!", e.getMessage());
    }

    @Test
    public void testSendTaskToPriorityQueue() {

        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setGroupId("testgroup");
        taskModel.setDeadline(OffsetDateTime.MAX);
        TaskModelMeta meta = new TaskModelMeta();
        meta.setKey("test");
        meta.setValue("tset");
        taskModel.setMeta(Collections.singletonList(meta));
        taskModel.setPriority(1);
        taskModel.setIndexNumber(1);
        taskModel.setRetries(9);
        taskModel.setTypeFlag(TaskModel.TypeFlagEnum.BATCH);
        taskService.sendTaskToPriorityQueue(taskModel);
        verify(communication,Mockito.only()).sendTaskToPriorityQueue(Mockito.any());
    }
    @Test
    public void testSendTaskToPriorityQueue_NullpointerCheck() {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setPriority(1);
        taskService.sendTaskToPriorityQueue(taskModel);
        verify(communication,Mockito.only()).sendTaskToPriorityQueue(Mockito.any());
    }
}
