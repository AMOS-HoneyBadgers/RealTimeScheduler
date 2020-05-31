package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.Group;
import com.honeybadgers.models.Task;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.repository.GroupRepository;
import com.honeybadgers.taskapi.repository.TaskRepository;
import com.honeybadgers.taskapi.service.impl.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    ITaskService taskService;


    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    }

    @Test
    public void testCreateTask() throws JpaException, UnknownEnumException {

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Task t = taskService.createTask(restModel);

        assertNotNull(t);
        assertThat(t.getId()).isEqualTo(restModel.getId().toString());
    }

    @Test
    public void testCreateTaskGroup404() {

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroupNotFound");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals(e.getMessage(), "Group not found!");
    }

    @Test
    public void testCreateTaskPrimaryKeyViolation() {

        DataIntegrityViolationException vio = new DataIntegrityViolationException("primary key violation");

        when(taskRepository.save(any(Task.class))).thenThrow(vio);

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals(e.getMessage(), "Primary or unique constraint failed!");
    }
}
